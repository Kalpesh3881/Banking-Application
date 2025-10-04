package com.example.bankpay.domain.exception;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain-level exception (no framework/HTTP specifics).
 * Carries a stable {@link ErrorCode} and optional metadata for context.
 */
public class DomainException extends RuntimeException {

    private final ErrorCode code;
    private final Map<String, Object> metadata;

    public DomainException(ErrorCode code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.metadata = Collections.emptyMap();
    }

    public DomainException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.metadata = Collections.emptyMap();
    }

    public DomainException(ErrorCode code, String message, Map<String, ?> metadata) {
        super(message);
        this.code = Objects.requireNonNull(code, "code must not be null");
        Map<String, Object> copy = new LinkedHashMap<>();
        if (metadata != null) copy.putAll(metadata);
        this.metadata = Collections.unmodifiableMap(copy);
    }

    public ErrorCode code() {
        return code;
    }

    /** Additional context (e.g., email, userId, retryAfterSeconds). */
    public Map<String, Object> metadata() {
        return metadata;
    }

    public boolean is(ErrorCode other) {
        return this.code == other;
    }

    /* ---------- Common factory helpers for onboarding/OTP ---------- */

    public static DomainException duplicateEmail(String email) {
        return new DomainException(
                ErrorCode.DUPLICATE_EMAIL,
                "Email already registered",
                Map.of("email", email)
        );
    }

    public static DomainException userNotFound(Long userId) {
        return new DomainException(
                ErrorCode.USER_NOT_FOUND,
                "User not found",
                Map.of("userId", userId)
        );
    }

    public static DomainException otpInvalid(Long userId) {
        return new DomainException(
                ErrorCode.OTP_INVALID,
                "Invalid OTP code",
                Map.of("userId", userId)
        );
    }

    public static DomainException otpExpired(Long userId) {
        return new DomainException(
                ErrorCode.OTP_EXPIRED,
                "OTP code expired",
                Map.of("userId", userId)
        );
    }

    public static DomainException rateLimited(String scope, Duration retryAfter) {
        long seconds = retryAfter == null ? 0 : retryAfter.toSeconds();
        return new DomainException(
                ErrorCode.RATE_LIMITED,
                "Too many attempts. Please try again later.",
                Map.of("scope", scope, "retryAfterSeconds", seconds)
        );
    }
}

