package com.example.bankpay.domain.event;


import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    Instant occurredAt();
    String type(); // stable event name, e.g. "user.registered"
}
