package com.example.bankpay.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Payload to create a new (disabled) user and start onboarding via OTP.
 */
public record RegisterRequest(
        @NotBlank @Email
        String email,

        @Pattern(regexp = "^[+]?\\d{7,15}$", message = "must be a valid phone number")
        String phone,

        @NotBlank
        @Size(min = 8, max = 72, message = "password must be 8–72 characters")
        String password,

        @Size(max = 80)
        String firstName,

        @Size(max = 80)
        String lastName
) {}
