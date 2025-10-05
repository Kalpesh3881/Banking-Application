package com.example.bankpay.service.port;

import com.example.bankpay.domain.model.Payment;

import java.util.Optional;

public interface PaymentGateway {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    Payment create(Payment payment);        // returns with id
    Payment update(Payment payment);        // status changes
}
