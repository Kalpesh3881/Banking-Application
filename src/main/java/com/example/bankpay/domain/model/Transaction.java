package com.example.bankpay.domain.model;

import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.domain.enums.TransactionType;

import java.time.Instant;
import java.util.Objects;

/**
 * Minimal ledger transaction tied to a single account.
 * Payments will create one or more Transactions.
 */
public record Transaction(
        Long id,
        Long accountId,
        TransactionType type,
        String currency,       // must match account currency
        long amountMinor,      // >= 0
        String reference,      // free text / statement descriptor
        String counterparty,   // e.g., IBAN/BIC or payee name
        TransactionStatus status,
        Instant valueDate,     // when it takes effect on balance
        Instant createdAt,
        String correlationId   // to dedupe / trace (same across stages)
) {
    public Transaction {
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(valueDate, "valueDate");
        Objects.requireNonNull(createdAt, "createdAt");
        if (amountMinor < 0) throw new IllegalArgumentException("amountMinor must be >= 0");
        currency = currency.trim().toUpperCase();
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be ISO-4217 code");
    }

    public static Transaction posted(Long accountId,
                                     TransactionType type,
                                     Money amount,
                                     String reference,
                                     String counterparty,
                                     Instant valueDate,
                                     Instant now,
                                     String correlationId) {
        return new Transaction(
                null, accountId, type, amount.currency(), amount.minor(),
                reference, counterparty, TransactionStatus.POSTED, valueDate, now, correlationId
        );
    }

    public Transaction withId(Long newId) {
        return new Transaction(newId, accountId, type, currency, amountMinor, reference, counterparty, status, valueDate, createdAt, correlationId);
    }
}
