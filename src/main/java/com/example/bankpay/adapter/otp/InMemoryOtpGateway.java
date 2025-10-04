package com.example.bankpay.adapter.otp;

import com.example.bankpay.domain.enums.OtpPurpose;
import com.example.bankpay.service.port.OtpGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("dev-inmem")
public class InMemoryOtpGateway implements OtpGateway {
    private record Key(Long userId, OtpPurpose purpose) {}
    private static class Token {
        String code; Instant expiresAt; boolean used; Instant lastIssuedAt;
        Token(String code, Instant expiresAt, Instant lastIssuedAt) {
            this.code = code; this.expiresAt = expiresAt; this.lastIssuedAt = lastIssuedAt;
        }
    }
    private final Map<Key, Token> store = new ConcurrentHashMap<>();

    @Override
    public void createToken(Long userId, OtpPurpose purpose, String code, Instant expiresAt) {
        store.put(new Key(userId, purpose), new Token(code, expiresAt, Instant.now()));
    }

    @Override
    public boolean verifyAndConsume(Long userId, OtpPurpose purpose, String code, Instant now) {
        Token t = store.get(new Key(userId, purpose));
        if (t == null || t.used || t.expiresAt.isBefore(now) || !t.code.equals(code)) return false;
        t.used = true;
        return true;
    }

    @Override
    public Instant nextIssueAllowedAt(Long userId, OtpPurpose purpose, Instant now) {
        Token t = store.get(new Key(userId, purpose));
        if (t == null || t.lastIssuedAt == null) return null;
        Instant allowed = t.lastIssuedAt.plusSeconds(30);
        return allowed.isAfter(now) ? allowed : null;
    }
}
