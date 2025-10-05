package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.persistence.mongo.doc.PaymentDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<PaymentDoc, Long> {
    Optional<PaymentDoc> findByIdempotencyKey(String idempotencyKey);
}
