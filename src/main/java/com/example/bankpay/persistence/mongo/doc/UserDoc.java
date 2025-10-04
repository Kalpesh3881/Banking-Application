package com.example.bankpay.persistence.mongo.doc;

import com.example.bankpay.domain.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "users")
public class UserDoc {
    @Id
    public Long id;
    @Indexed(unique = true)
    public String email;           // store lowercased
    public String phone;
    public String passwordHash;
    public boolean enabled;
    public Instant createdAt;
    public Set<Role> roles;
}
