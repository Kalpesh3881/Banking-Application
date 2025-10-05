package com.example.bankpay.domain.dto;

import java.util.List;

/** Simple wrapper for list endpoints (can be extended with paging later). */
public record AccountsListResponse(
        List<AccountResponse> items
) {}
