package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.PaymentStatus;

import java.time.Instant;

public record PaymentResponse(
        Long paymentId,
        PaymentStatus status,
        String correlationId,
        Instant createdAt,
        Instant postedAt
) {}
