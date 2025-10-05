package com.example.bankpay.adapter.account;

import com.example.bankpay.domain.model.Account;
import com.example.bankpay.persistence.mongo.SequenceGenerator;
import com.example.bankpay.persistence.mongo.doc.AccountDoc;
import com.example.bankpay.persistence.mongo.repo.AccountRepository;
import com.example.bankpay.service.port.AccountGateway;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("!dev-inmem") // active unless you run with profile 'dev-inmem'
public class MongoAccountGateway implements AccountGateway {

    private final AccountRepository repo;
    private final SequenceGenerator seq;

    public MongoAccountGateway(AccountRepository repo, SequenceGenerator seq) {
        this.repo = repo;
        this.seq = seq;
    }

    @Override
    public Account create(Account account) {
        long id = seq.next("accounts");
        AccountDoc doc = toDoc(account.withId(id));
        repo.save(doc);
        return toDomain(doc);
    }

    @Override
    public Account update(Account account) {
        AccountDoc doc = toDoc(account);
        repo.save(doc);
        return toDomain(doc);
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return repo.findById(accountId).map(this::toDomain);
    }

    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return repo.findByCustomerId(customerId).stream().map(this::toDomain).toList();
    }

    /* ---------- mapping ---------- */

    private AccountDoc toDoc(Account a) {
        AccountDoc d = new AccountDoc();
        d.id = a.id();
        d.customerId = a.customerId();
        d.accountNo = a.accountNo();
        d.iban = a.iban();
        d.type = a.type();
        d.currency = a.currency();
        d.status = a.status();
        d.balanceMinor = a.balanceMinor();
        d.overdraftLimitMinor = a.overdraftLimitMinor();
        d.openedAt = a.openedAt();
        d.closedAt = a.closedAt();
        return d;
    }

    private Account toDomain(AccountDoc d) {
        return new Account(
                d.id, d.customerId, d.accountNo, d.iban, d.type, d.currency, d.status,
                d.balanceMinor, d.overdraftLimitMinor, d.openedAt, d.closedAt
        );
    }
}
