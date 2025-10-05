package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.AccountType;
import jakarta.validation.constraints.*;

public record OpenAccountRequest(
        @NotNull
        Long customerId,

        @NotNull
        AccountType type,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "currency must be 3-letter ISO code")
        String currency,

        /** Overdraft limit in minor units (e.g., cents); 0 for no overdraft */
        @PositiveOrZero
        Long overdraftLimitMinor,

        /** Optional internal account number (bank-specific format) */
        @Size(max = 34)
        String accountNo,

        /** Optional IBAN; basic length guard only (detailed IBAN validation can be added later) */
        @Size(min = 15, max = 34)
        String iban
) {}
