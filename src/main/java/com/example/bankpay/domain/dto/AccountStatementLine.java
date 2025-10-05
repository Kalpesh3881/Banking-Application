package com.example.bankpay.domain.dto;

import java.time.Instant;

public record AccountStatementLine(
        Long transactionId,
        Instant valueDate,
        String type,              // "DEBIT" or "CREDIT"
        Long amountMinor,
        Long balanceAfterMinor,
        String reference,
        String counterparty,
        String correlationId
) {}
