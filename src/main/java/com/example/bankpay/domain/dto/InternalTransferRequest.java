package com.example.bankpay.domain.dto;

import jakarta.validation.constraints.*;

public record InternalTransferRequest(
        @NotNull Long sourceAccountId,
        @NotNull Long destAccountId,

        /** Minor units (e.g., cents). */
        @Positive Long amountMinor,

        /** ISO-4217 currency (must match both accounts). */
        @NotBlank @Pattern(regexp = "^[A-Za-z]{3}$") String currency,

        /** Free-form statement descriptor. */
        @Size(max = 140) String reference
) {}
