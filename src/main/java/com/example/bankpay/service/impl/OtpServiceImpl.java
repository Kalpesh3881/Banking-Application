package com.example.bankpay.service.impl;


import com.example.bankpay.domain.dto.OtpIssueResponse;
import com.example.bankpay.domain.enums.OtpPurpose;
import com.example.bankpay.service.OtpService;
import com.example.bankpay.service.port.OtpGateway;
import com.example.bankpay.service.port.UserGateway;
import com.example.bankpay.support.ClockProvider;
import com.example.bankpay.domain.dto.RequestOtpRequest;
import com.example.bankpay.domain.dto.VerifyOtpRequest;
import com.example.bankpay.domain.dto.VerifyOtpResponse;
import com.example.bankpay.domain.exception.DomainException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Service
public class OtpServiceImpl implements OtpService {

    // Basic defaults; can be externalized to properties later
    private static final int OTP_LENGTH = 4;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration ISSUE_MIN_GAP = Duration.ofSeconds(30); // simple rate limit

    private final OtpGateway otps;
    private final UserGateway users;
    private final ClockProvider clock;
    private final SecureRandom random = new SecureRandom();

    public OtpServiceImpl(OtpGateway otps, UserGateway users, ClockProvider clock) {
        this.otps = otps;
        this.users = users;
        this.clock = clock;
    }

    @Override
//    @Transactional
    public OtpIssueResponse issueOtp(RequestOtpRequest request) {
        Long userId = Objects.requireNonNull(request.userId(), "userId required");
        OtpPurpose purpose = request.purpose() == null ? OtpPurpose.REGISTER : request.purpose();

        // ensure user exists
        users.findById(userId).orElseThrow(() -> DomainException.userNotFound(userId));

        Instant now = clock.now();

        // simple rate-limit
        Instant nextAllowed = otps.nextIssueAllowedAt(userId, purpose, now);
        if (nextAllowed != null && nextAllowed.isAfter(now)) {
            Duration wait = Duration.between(now, nextAllowed);
            throw DomainException.rateLimited("OTP_ISSUE_" + purpose.name(), wait);
        }

        String code = generateNumericCode(OTP_LENGTH);
        Instant expiresAt = now.plus(OTP_TTL);

        otps.createToken(userId, purpose, code, expiresAt);
        // real implementation could send SMS/Email here via a NotificationPort
        System.out.println("DEBUG: OTP for userId=" + userId + " purpose=" + purpose + " is: " + code);

        return OtpIssueResponse.sent(userId, OTP_TTL);
    }

    @Override
//    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        Long userId = Objects.requireNonNull(request.userId(), "userId required");
        String code = Objects.requireNonNull(request.code(), "code required");
        OtpPurpose purpose = request.purpose() == null ? OtpPurpose.REGISTER : request.purpose();

        Instant now = clock.now();
        boolean ok = otps.verifyAndConsume(userId, purpose, code, now);
        if (!ok) {
            // Adapter decides whether it failed due to expiry or mismatch. We surface a generic error.
            throw DomainException.otpInvalid(userId);
        }

        if (purpose == OtpPurpose.REGISTER) {
            users.enableUser(userId);
        }

        return new VerifyOtpResponse(userId, "VERIFIED");
    }

    private String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        // first digit 1-9 to avoid leading zeros UX confusion; then 0-9
        sb.append(1 + random.nextInt(9));
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
