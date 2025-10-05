package com.example.bankpay.domain.dto;

public record DepositResponse(
        Long accountId,
        Long transactionId,
        Long newBalanceMinor,
        String status // "POSTED"
) {}
