package com.example.bankpay.adapter.transaction;

import com.example.bankpay.domain.enums.TransactionType;
import com.example.bankpay.domain.model.Transaction;
import com.example.bankpay.domain.enums.TransactionStatus;
import com.example.bankpay.persistence.mongo.doc.TransactionDoc;
import com.example.bankpay.persistence.mongo.repo.TransactionRepository;
import com.example.bankpay.service.port.TransactionGateway;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MongoTransactionGateway implements TransactionGateway {

    private final TransactionRepository repo;
    private final MongoTemplate template;

    public MongoTransactionGateway(TransactionRepository repo, MongoTemplate template) {
        this.repo = repo;
        this.template = template;
    }

    @Override
    public List<Transaction> findPostedBetween(Long accountId, Instant fromInclusive, Instant toInclusive) {
        var docs = repo.findByAccountIdAndStatusAndValueDateBetweenOrderByValueDateAscCreatedAtAsc(
                accountId, TransactionStatus.POSTED, fromInclusive, toInclusive);
        return docs.stream().map(this::toDomain).toList();
    }

    @Override
    public long netChangeBefore(Long accountId, Instant beforeInstant) {
        CriteriaDefinition match = new Criteria().andOperator(
                Criteria.where("accountId").is(accountId),
                Criteria.where("status").is(TransactionStatus.POSTED),
                Criteria.where("valueDate").lt(beforeInstant)
        );

        // sum credits
        var creditAgg = Aggregation.newAggregation(
                Aggregation.match(match),
                Aggregation.match(Criteria.where("type").is(TransactionType.CREDIT)),
                Aggregation.group().sum("amountMinor").as("sum")
        );
        var debitAgg = Aggregation.newAggregation(
                Aggregation.match(match),
                Aggregation.match(Criteria.where("type").is(TransactionType.DEBIT)),
                Aggregation.group().sum("amountMinor").as("sum")
        );

        Long credits = template.aggregate(creditAgg, "transactions", SumResult.class)
                .getUniqueMappedResult() == null ? 0L :
                template.aggregate(creditAgg, "transactions", SumResult.class).getUniqueMappedResult().sum;
        Long debits = template.aggregate(debitAgg, "transactions", SumResult.class)
                .getUniqueMappedResult() == null ? 0L :
                template.aggregate(debitAgg, "transactions", SumResult.class).getUniqueMappedResult().sum;

        return credits - debits;
    }

    private static class SumResult { public Long sum; }

    private Transaction toDomain(TransactionDoc d) {
        return new Transaction(
                d.id, d.accountId, d.type, d.currency, d.amountMinor,
                d.reference, d.counterparty, d.status, d.valueDate, d.createdAt, d.correlationId
        );
    }
}
