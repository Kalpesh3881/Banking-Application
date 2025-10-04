package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.enums.OtpPurpose;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * keeping a single active OTP per (userId,purpose) by using a composite string key.
 * That simplifies rate-limiting and verification logic.
 */
@Document(collection = "otp_tokens")
public class OtpTokenDoc {
    @Id
    public String id;                // format: userId + ":" + purpose
    @Indexed
    public Long userId;
    public OtpPurpose purpose;
    public String hashedCode;        // BCrypt hash of OTP
    @Indexed(expireAfterSeconds = 0) // Mongo TTL: document removed at expiresAt
    public Instant expiresAt;
    public boolean used;
    public Instant createdAt;
    public Instant usedAt;
    public Instant lastIssuedAt;     // for rate-limiting
}
