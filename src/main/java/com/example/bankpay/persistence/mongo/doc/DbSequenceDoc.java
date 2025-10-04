package com.example.bankpay.persistence.mongo.doc;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "seq")
public class DbSequenceDoc {
    @Id
    public String name; // e.g., "users", "customer_profiles"
    public long value;
}
