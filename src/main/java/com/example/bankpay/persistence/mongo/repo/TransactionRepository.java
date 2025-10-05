package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.persistence.mongo.doc.TransactionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends MongoRepository<TransactionDoc, Long> {

    List<TransactionDoc> findByAccountIdAndStatusAndValueDateBetweenOrderByValueDateAscCreatedAtAsc(
            Long accountId, TransactionStatus status, Instant from, Instant to
    );
}
