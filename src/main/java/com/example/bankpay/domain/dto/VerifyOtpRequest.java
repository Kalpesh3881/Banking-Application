package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Verify OTP to complete onboarding and enable the user.
 */
public record VerifyOtpRequest(
        @NotNull
        Long userId,

        @NotBlank
        @Pattern(regexp = "^\\d{4}$", message = "OTP must be 4 digit")
        String code,

        OtpPurpose purpose
) {}
