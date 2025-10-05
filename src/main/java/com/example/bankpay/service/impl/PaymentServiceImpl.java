package com.example.bankpay.service.impl;

import com.example.bankpay.domain.dto.InternalTransferRequest;
import com.example.bankpay.domain.dto.PaymentResponse;
import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.domain.enums.TransactionType;
import com.example.bankpay.domain.exception.DomainException;
import com.example.bankpay.domain.model.*;
import com.example.bankpay.service.PaymentService;
import com.example.bankpay.service.port.AccountGateway;
import com.example.bankpay.service.port.PaymentGateway;
import com.example.bankpay.service.port.TransactionGateway;
import com.example.bankpay.support.ClockProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.bankpay.domain.exception.ErrorCode.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final AccountGateway accounts;
    private final TransactionGateway txns;
    private final PaymentGateway payments;
    private final ClockProvider clock;

    public PaymentServiceImpl(AccountGateway accounts,
                              TransactionGateway txns,
                              PaymentGateway payments,
                              ClockProvider clock) {
        this.accounts = accounts;
        this.txns = txns;
        this.payments = payments;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PaymentResponse internalTransfer(InternalTransferRequest req, String idempotencyKey) {
        String idem = Objects.requireNonNull(idempotencyKey, "Idempotency-Key required").trim();
        if (idem.isBlank()) throw new IllegalArgumentException("Idempotency-Key cannot be blank");

        // Idempotency replay
        var existing = payments.findByIdempotencyKey(idem);
        if (existing.isPresent()) {
            var p = existing.get();
            return new PaymentResponse(p.id(), p.status(), p.correlationId(), p.createdAt(), p.postedAt());
        }

        Instant now = clock.now();
        String corr = idem; // reuse as correlationId so txns can be deduped across retries

        // Load accounts
        Account src = accounts.findById(req.sourceAccountId())
                .orElseThrow(() -> new DomainException(ACCOUNT_NOT_FOUND, "Source account not found",
                        java.util.Map.of("accountId", req.sourceAccountId())));
        Account dst = accounts.findById(req.destAccountId())
                .orElseThrow(() -> new DomainException(ACCOUNT_NOT_FOUND, "Destination account not found",
                        java.util.Map.of("accountId", req.destAccountId())));

        if (src.id().equals(dst.id())) {
            throw new DomainException(SAME_ACCOUNT_TRANSFER_INVALID, "Cannot transfer to the same account",
                    java.util.Map.of("accountId", src.id()));
        }
        if (!src.currency().equalsIgnoreCase(req.currency()) || !dst.currency().equalsIgnoreCase(req.currency())) {
            throw new DomainException(CURRENCY_MISMATCH, "Currency mismatch across accounts and request",
                    java.util.Map.of("sourceCurrency", src.currency(), "destCurrency", dst.currency(), "requestCurrency", req.currency()));
        }
        if (src.status() != AccountStatus.ACTIVE || dst.status() == AccountStatus.CLOSED) {
            throw new DomainException(ACCOUNT_STATE_INVALID, "Account status does not allow transfer",
                    java.util.Map.of("srcStatus", src.status().name(), "dstStatus", dst.status().name()));
        }

        // Create INITIATED payment row
        Payment payment = payments.create(Payment.init(
                src.id(), dst.id(), src.currency(), req.amountMinor(), req.reference(), idem, corr, now));

        // Apply domain debits/credits
        Money amount = Money.of(src.currency(), req.amountMinor());
        Account debited;
        try {
            debited = src.debit(amount);
        } catch (IllegalStateException ex) {
            payments.update(payment.failed("INSUFFICIENT_FUNDS"));
            throw new DomainException(INSUFFICIENT_FUNDS, "Insufficient funds / overdraft exceeded",
                    java.util.Map.of("accountId", src.id()));
        }
        Account credited = dst.credit(amount);

        // Persist: balances + transactions + payment status (atomic via Mongo transaction)
        accounts.update(debited);
        accounts.update(credited);

        Transaction tOut = Transaction.posted(src.id(), TransactionType.DEBIT, amount,
                req.reference(), "ACC:" + dst.id(), now, now, corr);
        Transaction tIn = Transaction.posted(dst.id(), TransactionType.CREDIT, amount,
                req.reference(), "ACC:" + src.id(), now, now, corr);
        txns.createAll(List.of(tOut, tIn));

        Payment posted = payments.update(payment.posted(now));
        return new PaymentResponse(posted.id(), posted.status(), posted.correlationId(), posted.createdAt(), posted.postedAt());
    }
}
