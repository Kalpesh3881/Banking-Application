package com.example.bankpay.domain.dto;

/**
 * Returned after registration. User is created but disabled until OTP verification.
 */
public record RegisterResponse(
        Long userId,
        String status // e.g., "PENDING_VERIFICATION"
) {
    public static RegisterResponse pending(Long userId) {
        return new RegisterResponse(userId, "PENDING_VERIFICATION");
    }
}

