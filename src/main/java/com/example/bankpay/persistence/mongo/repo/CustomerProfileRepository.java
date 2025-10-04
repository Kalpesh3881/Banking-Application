package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.persistence.mongo.doc.CustomerProfileDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerProfileRepository extends MongoRepository<CustomerProfileDoc, Long> {}
