package com.example.bankpay.service.impl;

import com.example.bankpay.domain.dto.DepositRequest;
import com.example.bankpay.domain.dto.DepositResponse;
import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.domain.enums.TransactionType;
import com.example.bankpay.domain.exception.DomainException;
import com.example.bankpay.domain.model.*;
import com.example.bankpay.service.FundingService;
import com.example.bankpay.service.port.AccountGateway;
import com.example.bankpay.service.port.TransactionGateway;
import com.example.bankpay.support.ClockProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

import static com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_NOT_FOUND;
import static com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_STATE_INVALID;
import static com.example.bankpay.domain.exception.ErrorCode.CURRENCY_MISMATCH;

@Service
public class FundingServiceImpl implements FundingService {

    private final AccountGateway accounts;
    private final TransactionGateway txns;
    private final ClockProvider clock;

    public FundingServiceImpl(AccountGateway accounts, TransactionGateway txns, ClockProvider clock) {
        this.accounts = accounts;
        this.txns = txns;
        this.clock = clock;
    }

    @Override
//    @Transactional
    public DepositResponse deposit(Long accountId, DepositRequest req, String idempotencyKey) {
        String idem = Objects.requireNonNull(idempotencyKey, "Idempotency-Key required").trim();
        if (idem.isBlank()) throw new IllegalArgumentException("Idempotency-Key cannot be blank");

        // idempotency: if we already posted this deposit, return existing txn info
        var existingTxn = txns.findByAccountAndCorrelation(accountId, idem);
        if (existingTxn.isPresent()) {
            var acc = accounts.findById(accountId).orElseThrow(() -> new DomainException(
                    ACCOUNT_NOT_FOUND, "Account not found", java.util.Map.of("accountId", accountId)));
            return new DepositResponse(accountId, existingTxn.get().id(), acc.balanceMinor(), "POSTED");
        }

        Account acc = accounts.findById(accountId).orElseThrow(() ->
                new DomainException(ACCOUNT_NOT_FOUND, "Account not found",
                        java.util.Map.of("accountId", accountId)));

        // Allow deposit into ACTIVE or FROZEN; block CLOSED
        if (acc.status() == AccountStatus.CLOSED) {
            throw new DomainException(ACCOUNT_STATE_INVALID, "Cannot deposit to a CLOSED account",
                    java.util.Map.of("status", acc.status().name()));
        }
        if (!acc.currency().equalsIgnoreCase(req.currency())) {
            throw new DomainException(CURRENCY_MISMATCH, "Currency mismatch between account and request",
                    java.util.Map.of("accountCurrency", acc.currency(), "requestCurrency", req.currency()));
        }

        Instant now = clock.now();
        Money amount = Money.of(acc.currency(), req.amountMinor());

        // credit account
        Account credited = acc.credit(amount);
        accounts.update(credited);

        // write CREDIT transaction (counterparty: CASH:SELF)
        var txn = Transaction.posted(
                credited.id(),
                TransactionType.CREDIT,
                amount,
                req.reference(),
                "CASH:SELF",
                now, now,
                idem // correlationId used for idempotency
        );
        var saved = txns.create(txn);

        return new DepositResponse(credited.id(), saved.id(), credited.balanceMinor(), "POSTED");
    }
}
