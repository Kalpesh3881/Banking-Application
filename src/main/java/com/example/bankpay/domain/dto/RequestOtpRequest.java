package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.OtpPurpose;
import jakarta.validation.constraints.NotNull;

/**
 * Request (re)issuing an OTP for a purpose (defaults to REGISTER in the service layer).
 */
public record RequestOtpRequest(
        @NotNull
        Long userId,

        // Optional; if null the service may default to OtpPurpose.REGISTER
        OtpPurpose purpose
) {}
