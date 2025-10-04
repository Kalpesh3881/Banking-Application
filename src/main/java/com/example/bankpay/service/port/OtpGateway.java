package com.example.bankpay.service.port;

import com.example.bankpay.domain.enums.OtpPurpose;

import java.time.Instant;

public interface OtpGateway {
    /** Persist a new OTP (ideally hashed inside the gateway/adapter). */
    void createToken(Long userId, OtpPurpose purpose, String code, Instant expiresAt);

    /**
     * Verify and consume an OTP atomically:
     * - must exist for user & purpose
     * - not expired, not used
     * - code matches (adapter decides hashing strategy)
     * Returns true if consumed successfully.
     */
    boolean verifyAndConsume(Long userId, OtpPurpose purpose, String code, Instant now);

    /**
     * Optional simple rate limit: return epoch second when issuing is allowed again.
     * Return null if allowed immediately.
     */
    Instant nextIssueAllowedAt(Long userId, OtpPurpose purpose, Instant now);
}
