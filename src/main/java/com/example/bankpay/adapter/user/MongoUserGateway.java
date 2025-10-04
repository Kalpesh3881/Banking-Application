package com.example.bankpay.adapter.user;

import com.example.bankpay.domain.model.User;
import com.example.bankpay.persistence.mongo.SequenceGenerator;
import com.example.bankpay.persistence.mongo.doc.CustomerProfileDoc;
import com.example.bankpay.persistence.mongo.doc.UserDoc;
import com.example.bankpay.persistence.mongo.repo.CustomerProfileRepository;
import com.example.bankpay.persistence.mongo.repo.UserRepository;
import com.example.bankpay.service.port.UserGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!dev-inmem") // disable if you run the in-memory gateways
public class MongoUserGateway implements UserGateway {

    private final UserRepository users;
    private final CustomerProfileRepository profiles;
    private final SequenceGenerator seq;

    public MongoUserGateway(UserRepository users,
                            CustomerProfileRepository profiles,
                            SequenceGenerator seq) {
        this.users = users;
        this.profiles = profiles;
        this.seq = seq;
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.existsByEmail(normalizeEmail(email));
    }

    @Override
    public User create(User user, String firstName, String lastName) {
        long userId = seq.next("users");
        UserDoc doc = toDoc(user.withId(userId));
        doc.email = normalizeEmail(doc.email);
        users.save(doc);

        long profileId = seq.next("customer_profiles");
        CustomerProfileDoc profile = new CustomerProfileDoc();
        profile.id = profileId;
        profile.userId = userId;
        profile.firstName = firstName;
        profile.lastName = lastName;
        profile.kycStatus = com.example.bankpay.domain.model.CustomerProfile.KycStatus.PENDING;
        profiles.save(profile);

        return toDomain(doc);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return users.findById(userId).map(this::toDomain);
    }

    @Override
    public void enableUser(Long userId) {
        users.findById(userId).ifPresent(doc -> {
            doc.enabled = true;
            users.save(doc);
        });
    }

    /* ---------- mapping ---------- */

    private UserDoc toDoc(User user) {
        UserDoc d = new UserDoc();
        d.id = user.id();
        d.email = user.email();
        d.phone = user.phone();
        d.passwordHash = user.passwordHash();
        d.enabled = user.enabled();
        d.createdAt = user.createdAt();
        d.roles = user.roles();
        return d;
    }

    private User toDomain(UserDoc d) {
        return new User(d.id, d.email, d.phone, d.passwordHash, d.enabled, d.createdAt, d.roles);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
