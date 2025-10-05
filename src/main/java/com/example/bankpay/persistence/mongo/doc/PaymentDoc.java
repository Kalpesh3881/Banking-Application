package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.enums.PaymentStatus;
import com.example.bankpay.domain.enums.PaymentType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
public class PaymentDoc {
    @Id public Long id;
    public PaymentType type;
    public Long sourceAccountId;
    public Long destAccountId;
    public String currency;
    public long amountMinor;
    public String reference;
    public PaymentStatus status;
    @Indexed(unique = true) public String idempotencyKey;
    @Indexed public String correlationId;
    public Instant createdAt;
    public Instant postedAt;
    public String failureReason;
}
