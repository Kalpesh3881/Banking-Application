package com.example.bankpay.domain.model;

import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.domain.enums.AccountType;

import java.time.Instant;
import java.util.Objects;

/**
 * Bank account aggregate (balance kept in minor units for accuracy).
 * Business invariants enforced in methods (e.g., overdraft checks).
 */
public record Account(
        Long id,
        Long customerId,
        String accountNo,          // internal account number (bank-specific format)
        String iban,               // optional
        AccountType type,
        String currency,           // ISO code; ALL amounts must match this
        AccountStatus status,
        long balanceMinor,         // current balance in minor units
        long overdraftLimitMinor,  // allowed negative range (e.g., -100_00)
        Instant openedAt,
        Instant closedAt
) {

    public Account {
        Objects.requireNonNull(customerId, "customerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(openedAt, "openedAt");
        currency = currency.trim().toUpperCase();
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be ISO-4217 code");
        if (overdraftLimitMinor < 0) throw new IllegalArgumentException("overdraftLimitMinor must be >= 0");
        if (status == AccountStatus.CLOSED && closedAt == null) {
            throw new IllegalArgumentException("closed accounts must have closedAt");
        }
    }

    /** Factory to open a fresh ACTIVE account with zero balance and given overdraft limit. */
    public static Account open(Long customerId,
                               AccountType type,
                               String currency,
                               String accountNo,
                               String iban,
                               long overdraftLimitMinor,
                               Instant now) {
        return new Account(
                null, customerId, normalize(accountNo), normalize(iban),
                type, currency,     AccountStatus.ACTIVE,
                0L, Math.max(0L, overdraftLimitMinor),
                now, null
        );
    }

    public Account withId(Long newId) {
        return new Account(newId, customerId, accountNo, iban, type, currency, status, balanceMinor, overdraftLimitMinor, openedAt, closedAt);
    }

    /** Credit increases balance. */
    public Account credit(Money amount) {
        requireCurrency(amount);
        long next = Math.addExact(this.balanceMinor, amount.minor());
        return new Account(id, customerId, accountNo, iban, type, currency, status, next, overdraftLimitMinor, openedAt, closedAt);
    }

    /** Debit decreases balance; must not exceed overdraft limit. */
    public Account debit(Money amount) {
        requireCurrency(amount);
        long next = Math.subtractExact(this.balanceMinor, amount.minor());
        if (next < -overdraftLimitMinor) {
            throw new IllegalStateException("insufficient funds (overdraft limit exceeded)");
        }
        return new Account(id, customerId, accountNo, iban, type, currency, status, next, overdraftLimitMinor, openedAt, closedAt);
    }

    public Account freeze() {
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("cannot freeze closed account");
        return new Account(id, customerId, accountNo, iban, type, currency, AccountStatus.FROZEN, balanceMinor, overdraftLimitMinor, openedAt, closedAt);
    }

    public Account unfreeze() {
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("cannot unfreeze closed account");
        return new Account(id, customerId, accountNo, iban, type, currency, AccountStatus.ACTIVE, balanceMinor, overdraftLimitMinor, openedAt, closedAt);
    }

    public Account close(Instant now) {
        if (status == AccountStatus.CLOSED) return this;
        return new Account(id, customerId, accountNo, iban, type, currency, AccountStatus.CLOSED, balanceMinor, overdraftLimitMinor, openedAt, Objects.requireNonNull(now));
    }

    private void requireCurrency(Money money) {
        if (!this.currency.equals(money.currency())) {
            throw new IllegalArgumentException("currency mismatch: account=%s, money=%s".formatted(this.currency, money.currency()));
        }
        if (money.minor() < 0) throw new IllegalArgumentException("amount must be non-negative");
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim();
    }
}
