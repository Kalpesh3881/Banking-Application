package com.example.bankpay.service.port;

import com.example.bankpay.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountGateway {
    /** Persist a newly opened account and return the saved aggregate (id assigned). */
    Account create(Account account);

    /** Update an existing account (status/balance/etc.) and return the saved aggregate. */
    Account update(Account account);

    Optional<Account> findById(Long accountId);

    /** List accounts for a customer (simple, no paging for now). */
    List<Account> findByCustomerId(Long customerId);
}
