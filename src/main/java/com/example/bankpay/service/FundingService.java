package com.example.bankpay.service;

import com.example.bankpay.domain.dto.DepositRequest;
import com.example.bankpay.domain.dto.DepositResponse;

public interface FundingService {
    DepositResponse deposit(Long accountId, DepositRequest request, String idempotencyKey);
}
