package com.example.bankpay.service;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.AccountsListResponse;
import com.example.bankpay.domain.dto.OpenAccountRequest;

public interface AccountService {
    AccountResponse open(OpenAccountRequest request);

    AccountResponse getById(Long accountId);

    AccountsListResponse listByCustomer(Long customerId);

    AccountResponse freeze(Long accountId);

    AccountResponse unfreeze(Long accountId);

    AccountResponse close(Long accountId);
}
