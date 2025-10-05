package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.domain.enums.AccountType;

import java.time.Instant;

public record AccountResponse(
        Long id,
        Long customerId,
        AccountType type,
        AccountStatus status,
        String currency,
        Long balanceMinor,
        Long overdraftLimitMinor,
        String accountNo,
        String iban,
        Instant openedAt,
        Instant closedAt
) {}
