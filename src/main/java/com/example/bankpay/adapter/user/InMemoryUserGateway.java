package com.example.bankpay.adapter.user;

import com.example.bankpay.domain.model.User;
import com.example.bankpay.service.port.UserGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Profile("dev-inmem")
public class InMemoryUserGateway implements UserGateway {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, User> byId = new ConcurrentHashMap<>();
    private final Map<String, Long> idByEmail = new ConcurrentHashMap<>();

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return idByEmail.containsKey(email.trim().toLowerCase());
    }

    @Override
    public User create(User user, String firstName, String lastName) {
        long id = seq.getAndIncrement();
        User saved = user.withId(id);
        byId.put(id, saved);
        idByEmail.put(saved.email(), id);
        return saved;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(byId.get(userId));
    }

    @Override
    public void enableUser(Long userId) {
        byId.computeIfPresent(userId, (id, u) -> u.enable());
    }
}
