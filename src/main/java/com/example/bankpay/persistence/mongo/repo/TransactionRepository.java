package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.persistence.mongo.doc.TransactionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<TransactionDoc, Long> {

    List<TransactionDoc> findByAccountIdAndStatusAndValueDateBetweenOrderByValueDateAscCreatedAtAsc(
            Long accountId, TransactionStatus status, Instant from, Instant to
    );

    Optional<TransactionDoc> findFirstByAccountIdAndCorrelationId(Long accountId, String correlationId);
}
