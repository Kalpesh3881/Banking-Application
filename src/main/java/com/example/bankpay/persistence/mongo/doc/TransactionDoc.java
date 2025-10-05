package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.domain.enums.TransactionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transactions")
public class TransactionDoc {
    @Id
    public Long id;

    @Indexed
    public Long accountId;

    public TransactionType type;     // DEBIT / CREDIT
    public String currency;
    public long amountMinor;
    public String reference;
    public String counterparty;
    public TransactionStatus status; // PENDING/POSTED/...
    @Indexed
    public Instant valueDate;
    public Instant createdAt;
    public String correlationId;
}
