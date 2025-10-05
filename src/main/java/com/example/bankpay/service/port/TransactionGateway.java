package com.example.bankpay.service.port;

import com.example.bankpay.domain.model.Transaction;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionGateway {
    /** Posted transactions in [from,to] ordered by (valueDate asc, createdAt asc). */
    List<Transaction> findPostedBetween(Long accountId, Instant fromInclusive, Instant toInclusive);

    /** Net balance change before 'beforeInstant' (credits - debits) for POSTED txns. */
    long netChangeBefore(Long accountId, Instant beforeInstant);

    Transaction create(Transaction txn);
    List<Transaction> createAll(List<Transaction> txns);

    Optional<Transaction> findByAccountAndCorrelation(Long accountId, String correlationId);
}
