package com.example.bankpay.adapter.otp;

import com.example.bankpay.domain.enums.OtpPurpose;
import com.example.bankpay.persistence.mongo.doc.OtpTokenDoc;
import com.example.bankpay.persistence.mongo.repo.OtpTokenRepository;
import com.example.bankpay.service.port.OtpGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Profile("!dev-inmem")
public class MongoOtpGateway implements OtpGateway {

    private final OtpTokenRepository repo;
    private final MongoTemplate template;
    private final PasswordEncoder passwordEncoder;

    public MongoOtpGateway(OtpTokenRepository repo,
                           MongoTemplate template,
                           PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.template = template;
        this.passwordEncoder = passwordEncoder;
    }

    private static String key(Long userId, OtpPurpose purpose) {
        return userId + ":" + purpose.name();
    }

    @Override
    public void createToken(Long userId, OtpPurpose purpose, String code, Instant expiresAt) {
        String id = key(userId, purpose);
        OtpTokenDoc doc = repo.findById(id).orElseGet(OtpTokenDoc::new);
        doc.id = id;
        doc.userId = userId;
        doc.purpose = purpose;
        doc.hashedCode = passwordEncoder.encode(code);
        doc.expiresAt = expiresAt;
        doc.used = false;
        doc.usedAt = null;
        doc.createdAt = Instant.now();
        doc.lastIssuedAt = Instant.now();
        repo.save(doc); // upsert
    }

    @Override
    public boolean verifyAndConsume(Long userId, OtpPurpose purpose, String code, Instant now) {
        // Read current active token
        OtpTokenDoc doc = repo.findById(key(userId, purpose)).orElse(null);
        if (doc == null) return false;
        if (doc.used || doc.expiresAt.isBefore(now)) return false;
        if (!passwordEncoder.matches(code, doc.hashedCode)) return false;

        // Atomic consume: only flip to used if still unused
        Query q = new Query(Criteria.where("_id").is(doc.id).and("used").is(false));
        Update u = new Update().set("used", true).set("usedAt", now);
        var res = template.updateFirst(q, u, OtpTokenDoc.class);
        return res.getModifiedCount() == 1;
    }

    @Override
    public Instant nextIssueAllowedAt(Long userId, OtpPurpose purpose, Instant now) {
        OtpTokenDoc doc = repo.findById(key(userId, purpose)).orElse(null);
        return (doc == null || doc.lastIssuedAt == null) ? null : doc.lastIssuedAt;
    }
}
