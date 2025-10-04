package com.example.bankpay.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Emitted after a user is created (disabled) as part of registration.
 * Useful for sending welcome emails, triggering KYC, analytics, etc.
 */
public record UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        Long userId,
        String email,
        String phone
) implements DomainEvent {

    public UserRegisteredEvent {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        email = email == null ? null : email.trim().toLowerCase();
        phone = phone == null ? null : phone.replaceAll("\\s+", "");
    }

    public static UserRegisteredEvent of(Long userId, String email, String phone, Instant now) {
        return new UserRegisteredEvent(UUID.randomUUID(), now, userId, email, phone);
    }

    @Override
    public String type() {
        return "user.registered";
    }
}
