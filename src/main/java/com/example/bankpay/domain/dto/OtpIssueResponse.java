package com.example.bankpay.domain.dto;

import java.time.Duration;

public record OtpIssueResponse(
        Long userId,
        String status,          // "OTP_SENT"
        long expiresInSeconds   // e.g., 300
) {
    public static OtpIssueResponse sent(Long userId, Duration ttl) {
        long secs = ttl == null ? 0 : ttl.toSeconds();
        return new OtpIssueResponse(userId, "OTP_SENT", secs);
    }
}

