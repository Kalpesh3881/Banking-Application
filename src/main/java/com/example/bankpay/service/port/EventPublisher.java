package com.example.bankpay.service.port;

import com.example.bankpay.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}
