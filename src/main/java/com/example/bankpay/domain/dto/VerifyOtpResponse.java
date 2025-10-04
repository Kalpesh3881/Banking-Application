package com.example.bankpay.domain.dto;

public record VerifyOtpResponse(
        Long userId,
        String status // "VERIFIED"
) {}

