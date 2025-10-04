package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.persistence.mongo.doc.OtpTokenDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpTokenRepository extends MongoRepository<OtpTokenDoc, String> {}
