package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.model.CustomerProfile.KycStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer_profiles")
public class CustomerProfileDoc {
    @Id
    public Long id;
    @Indexed
    public Long userId;
    public String firstName;
    public String lastName;
    public KycStatus kycStatus;
}
