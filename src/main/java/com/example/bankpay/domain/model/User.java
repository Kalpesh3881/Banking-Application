package com.example.bankpay.domain.model;

import com.example.bankpay.domain.enums.Role;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public record User(
        Long id,
        String email,
        String phone,
        String passwordHash,
        boolean enabled,
        Instant createdAt,
        Set<Role> roles
) {
    public User {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        roles = Collections.unmodifiableSet(roles == null ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles));
    }

    /** Factory for a just-registered (disabled) user with CUSTOMER role. */
    public static User newForRegistration(String email, String phone, String passwordHash, Instant now) {
        return new User(
                null,
                normalizeEmail(email),
                normalizePhone(phone),
                passwordHash,
                false,
                now,
                EnumSet.of(Role.CUSTOMER)
        );
    }

    /** Enable the user (after OTP verification). */
    public User enable() {
        return new User(id, email, phone, passwordHash, true, createdAt, roles);
    }

    /** Convenience to set an id after persistence. */
    public User withId(Long newId) {
        return new User(newId, email, phone, passwordHash, enabled, createdAt, roles);
    }

    /** Add a role (returns a new immutable instance). */
    public User addRole(Role role) {
        EnumSet<Role> next = EnumSet.copyOf(this.roles);
        next.add(role);
        return new User(id, email, phone, passwordHash, enabled, createdAt, next);
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\s+", "");
    }
}
