package com.example.bankpay.domain.model;

import com.example.bankpay.domain.enums.PaymentStatus;
import com.example.bankpay.domain.enums.PaymentType;

import java.time.Instant;
import java.util.Objects;

public record Payment(
        Long id,
        PaymentType type,
        Long sourceAccountId,
        Long destAccountId,
        String currency,
        long amountMinor,
        String reference,
        PaymentStatus status,
        String idempotencyKey,
        String correlationId,
        Instant createdAt,
        Instant postedAt,
        String failureReason
) {
    public Payment {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(sourceAccountId, "sourceAccountId");
        Objects.requireNonNull(destAccountId, "destAccountId");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        Objects.requireNonNull(correlationId, "correlationId");
        Objects.requireNonNull(createdAt, "createdAt");
        currency = currency.trim().toUpperCase();
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be ISO-4217");
    }

    public static Payment init(Long src, Long dst, String currency, long amtMinor, String ref,
                               String idemKey, String correlationId, Instant now) {
        return new Payment(null, PaymentType.INTERNAL_TRANSFER, src, dst, currency, amtMinor, ref,
                PaymentStatus.INITIATED, idemKey, correlationId, now, null, null);
    }

    public Payment posted(Instant now) {
        return new Payment(id, type, sourceAccountId, destAccountId, currency, amountMinor, reference,
                PaymentStatus.POSTED, idempotencyKey, correlationId, createdAt, now, null);
    }

    public Payment failed(String reason) {
        return new Payment(id, type, sourceAccountId, destAccountId, currency, amountMinor, reference,
                PaymentStatus.FAILED, idempotencyKey, correlationId, createdAt, postedAt, reason);
    }

    public Payment withId(Long newId) {
        return new Payment(newId, type, sourceAccountId, destAccountId, currency, amountMinor, reference,
                status, idempotencyKey, correlationId, createdAt, postedAt, failureReason);
    }
}
