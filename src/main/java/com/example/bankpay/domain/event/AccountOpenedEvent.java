package com.example.bankpay.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record AccountOpenedEvent(
        UUID eventId,
        Instant occurredAt,
        Long accountId,
        Long customerId,
        String currency,
        String type
) implements DomainEvent {

    public AccountOpenedEvent {
        Objects.requireNonNull(eventId);
        Objects.requireNonNull(occurredAt);
        Objects.requireNonNull(accountId);
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(type);
    }

    public static AccountOpenedEvent of(Long accountId, Long customerId, String currency, String type, Instant now) {
        return new AccountOpenedEvent(UUID.randomUUID(), now, accountId, customerId, currency, type);
    }

    @Override public String type() { return "account.opened"; }
}
