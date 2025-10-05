package com.example.bankpay.service;

import com.example.bankpay.domain.dto.AccountStatementResponse;

import java.time.Instant;

public interface StatementService {
    AccountStatementResponse generate(Long accountId, Instant fromInclusive, Instant toInclusive);
    byte[] exportCsv(AccountStatementResponse statement);
}
