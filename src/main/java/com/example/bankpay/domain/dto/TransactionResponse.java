package com.example.bankpay.domain.dto;

import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.domain.enums.TransactionType;

import java.time.Instant;

public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,     // DEBIT or CREDIT
        String currency,          // ISO-4217
        Long amountMinor,         // non-negative
        String reference,         // statement descriptor
        String counterparty,      // e.g., IBAN or name
        TransactionStatus status, // POSTED/REVERSED/...
        Instant valueDate,
        Instant createdAt,
        String correlationId
) {}
