package com.example.bankpay.domain.dto;

import java.time.Instant;
import java.util.List;

public record AccountStatementResponse(
        Long accountId,
        String currency,
        Instant from,                 // inclusive
        Instant to,                   // inclusive
        Long openingBalanceMinor,
        Long closingBalanceMinor,
        Long totalCreditsMinor,
        Long totalDebitsMinor,
        List<AccountStatementLine> lines
) {}
