package com.example.bankpay.persistence.mongo.repo;

import com.example.bankpay.persistence.mongo.doc.UserDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDoc, Long> {
    boolean existsByEmail(String email);
    Optional<UserDoc> findByEmail(String email);
}
