package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.enums.AccountStatus;
import com.example.bankpay.domain.enums.AccountType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "accounts")
public class AccountDoc {
    @Id public Long id;
    @Indexed public Long customerId;

    public String accountNo;
    public String iban;

    public AccountType type;
    public String currency; // ISO-4217
    public AccountStatus status;

    public long balanceMinor;
    public long overdraftLimitMinor;

    public Instant openedAt;
    public Instant closedAt;
}
