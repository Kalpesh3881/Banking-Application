package com.example.bankpay.domain.dto;

import jakarta.validation.constraints.*;

public record DomesticTransferRequest(
        @NotNull Long sourceAccountId,

        /** Minor units (e.g., cents) */
        @Positive Long amountMinor,

        /** ISO-4217 (must match source account) */
        @NotBlank @Pattern(regexp = "^[A-Za-z]{3}$") String currency,

        /** IBAN (basic check; we normalize by trimming/uppercasing) */
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9 ]{15,34}$",
                message = "IBAN must be 15–34 alphanumeric characters")
        String beneficiaryIban,

        /** Optional but recommended */
        @Size(max = 140) String beneficiaryName,

        /** Statement descriptor */
        @Size(max = 140) String reference
) {}
