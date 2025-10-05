package com.example.bankpay.service.impl;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.AccountsListResponse;
import com.example.bankpay.domain.dto.OpenAccountRequest;
import com.example.bankpay.domain.event.AccountOpenedEvent;
import com.example.bankpay.domain.exception.DomainException;
import com.example.bankpay.domain.model.Account;
import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.service.AccountService;
import com.example.bankpay.service.port.AccountGateway;
import com.example.bankpay.service.port.EventPublisher;
import com.example.bankpay.support.ClockProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountGateway accounts;
    private final EventPublisher events;
    private final ClockProvider clock;

    public AccountServiceImpl(AccountGateway accounts,
                              EventPublisher events,
                              ClockProvider clock) {
        this.accounts = accounts;
        this.events = events;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AccountResponse open(OpenAccountRequest req) {
        Instant now = clock.now();
        Account opened = Account.open(
                req.customerId(),
                req.type(),
                req.currency(),
                normalize(req.accountNo()),
                normalize(req.iban()),
                req.overdraftLimitMinor() == null ? 0L : req.overdraftLimitMinor(),
                now
        );

        Account saved = accounts.create(opened);

        // Publish event
        events.publish(AccountOpenedEvent.of(saved.id(), saved.customerId(), saved.currency(),
                saved.type().name(), now));

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getById(Long accountId) {
        Account acc = accounts.findById(accountId)
                .orElseThrow(() -> new DomainException(
                        com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found",
                        java.util.Map.of("accountId", accountId)
                ));
        return toDto(acc);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountsListResponse listByCustomer(Long customerId) {
        List<Account> list = accounts.findByCustomerId(customerId);
        return new AccountsListResponse(list.stream().map(this::toDto).toList());
    }

    @Override
    @Transactional
    public AccountResponse freeze(Long accountId) {
        Account acc = mustGet(accountId);
        if (acc.status() == AccountStatus.CLOSED) {
            throw new DomainException(
                    com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_STATE_INVALID,
                    "Cannot freeze a closed account",
                    java.util.Map.of("accountId", accountId, "status", acc.status().name())
            );
        }
        Account upd = accounts.update(acc.freeze());
        return toDto(upd);
    }

    @Override
    @Transactional
    public AccountResponse unfreeze(Long accountId) {
        Account acc = mustGet(accountId);
        if (acc.status() == AccountStatus.CLOSED) {
            throw new DomainException(
                    com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_STATE_INVALID,
                    "Cannot unfreeze a closed account",
                    java.util.Map.of("accountId", accountId, "status", acc.status().name())
            );
        }
        Account upd = accounts.update(acc.unfreeze());
        return toDto(upd);
    }

    @Override
    @Transactional
    public AccountResponse close(Long accountId) {
        Account acc = mustGet(accountId);
        if (acc.status() == AccountStatus.CLOSED) return toDto(acc);
        // Business rule: allow closing even if balance ≠ 0? For now we allow.
        Account upd = accounts.update(acc.close(clock.now()));
        return toDto(upd);
    }

    /* ---------------- helpers ---------------- */

    private Account mustGet(Long accountId) {
        return accounts.findById(accountId)
                .orElseThrow(() -> new DomainException(
                        com.example.bankpay.domain.exception.ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found",
                        java.util.Map.of("accountId", accountId)
                ));
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    private AccountResponse toDto(Account a) {
        return new AccountResponse(
                a.id(),
                a.customerId(),
                a.type(),
                a.status(),
                a.currency(),
                a.balanceMinor(),
                a.overdraftLimitMinor(),
                a.accountNo(),
                a.iban(),
                a.openedAt(),
                a.closedAt()
        );
    }
}
