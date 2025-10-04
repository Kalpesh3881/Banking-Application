package com.example.bankpay.domain.exception;

public enum ErrorCode {
    VALIDATION_FAILED,
    DUPLICATE_EMAIL,
    USER_NOT_FOUND,
    OTP_INVALID,
    OTP_EXPIRED,
    RATE_LIMITED
}
