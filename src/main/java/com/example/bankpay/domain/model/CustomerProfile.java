package com.example.bankpay.domain.model;

import java.util.Objects;

public record CustomerProfile(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        KycStatus kycStatus
) {
    public CustomerProfile {
        Objects.requireNonNull(userId, "userId must not be null");
        kycStatus = (kycStatus == null) ? KycStatus.PENDING : kycStatus;
    }

    public CustomerProfile approve() {
        return new CustomerProfile(id, userId, firstName, lastName, KycStatus.APPROVED);
    }

    public CustomerProfile reject() {
        return new CustomerProfile(id, userId, firstName, lastName, KycStatus.REJECTED);
    }

    public enum KycStatus { PENDING, APPROVED, REJECTED }
}
