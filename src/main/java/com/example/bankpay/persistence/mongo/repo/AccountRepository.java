package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.persistence.mongo.doc.AccountDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository<AccountDoc, Long> {
    List<AccountDoc> findByCustomerId(Long customerId);
}
