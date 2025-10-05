package com.example.bankpay.service.port;

import com.example.bankpay.domain.model.Transaction;

import java.time.Instant;
import java.util.List;

public interface TransactionGateway {
    /** Posted transactions in [from,to] ordered by (valueDate asc, createdAt asc). */
    List<Transaction> findPostedBetween(Long accountId, Instant fromInclusive, Instant toInclusive);

    /** Net balance change before 'beforeInstant' (credits - debits) for POSTED txns. */
    long netChangeBefore(Long accountId, Instant beforeInstant);
}
