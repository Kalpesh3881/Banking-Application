package com.example.bankpay.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Simple money value object using minor units (e.g., cents).
 * Currency is ISO-4217 code (e.g., "EUR", "USD").
 */
public record Money(String currency, long minor) {

    public Money {
        Objects.requireNonNull(currency, "currency must not be null");
        currency = currency.trim().toUpperCase();
        if (currency.length() != 3) throw new IllegalArgumentException("currency must be 3-letter ISO code");
    }

    public static Money of(String currency, long minor) {
        return new Money(currency, minor);
    }

    /** Create from decimal amount, scaling by fraction digits (2 by default). */
    public static Money fromDecimal(String currency, BigDecimal amount, int fractionDigits) {
        Objects.requireNonNull(amount, "amount");
        long minor = amount.setScale(fractionDigits, RoundingMode.HALF_UP)
                .movePointRight(fractionDigits).longValueExact();
        return new Money(currency, minor);
    }

    public BigDecimal toDecimal(int fractionDigits) {
        return BigDecimal.valueOf(minor, fractionDigits);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(currency, Math.addExact(this.minor, other.minor));
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(currency, Math.subtractExact(this.minor, other.minor));
    }

    public Money negate() {
        return new Money(currency, Math.negateExact(minor));
    }

    public boolean isNegative() { return minor < 0; }
    public boolean isZero() { return minor == 0; }

    private void requireSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("currency mismatch: %s vs %s".formatted(this.currency, other.currency));
        }
    }
}
