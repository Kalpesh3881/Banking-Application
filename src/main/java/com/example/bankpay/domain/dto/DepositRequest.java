package com.example.bankpay.domain.dto;

import jakarta.validation.constraints.*;

public record DepositRequest(
        /** Minor units (e.g., cents) */
        @Positive Long amountMinor,

        /** ISO-4217 code; must match the account */
        @NotBlank @Pattern(regexp = "^[A-Za-z]{3}$") String currency,

        /** Optional statement descriptor */
        @Size(max = 140) String reference
) {}
