package com.example.bankpay.adapter.payment;

import com.example.bankpay.domain.model.Payment;
import com.example.bankpay.persistence.mongo.SequenceGenerator;
import com.example.bankpay.persistence.mongo.doc.PaymentDoc;
import com.example.bankpay.persistence.mongo.repo.PaymentRepository;
import com.example.bankpay.service.port.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MongoPaymentGateway implements PaymentGateway {

    private final PaymentRepository repo;
    private final SequenceGenerator seq;

    public MongoPaymentGateway(PaymentRepository repo, SequenceGenerator seq) {
        this.repo = repo;
        this.seq = seq;
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return repo.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
    }

    @Override
    public Payment create(Payment payment) {
        PaymentDoc d = toDoc(payment.withId(seq.next("payments")));
        repo.save(d);
        return toDomain(d);
    }

    @Override
    public Payment update(Payment payment) {
        PaymentDoc d = toDoc(payment);
        repo.save(d);
        return toDomain(d);
    }

    private PaymentDoc toDoc(Payment p) {
        PaymentDoc d = new PaymentDoc();
        d.id = p.id();
        d.type = p.type();
        d.sourceAccountId = p.sourceAccountId();
        d.destAccountId = p.destAccountId();
        d.currency = p.currency();
        d.amountMinor = p.amountMinor();
        d.reference = p.reference();
        d.status = p.status();
        d.idempotencyKey = p.idempotencyKey();
        d.correlationId = p.correlationId();
        d.createdAt = p.createdAt();
        d.postedAt = p.postedAt();
        d.failureReason = p.failureReason();
        return d;
    }

    private Payment toDomain(PaymentDoc d) {
        return new Payment(d.id, d.type, d.sourceAccountId, d.destAccountId, d.currency, d.amountMinor,
                d.reference, d.status, d.idempotencyKey, d.correlationId, d.createdAt, d.postedAt, d.failureReason);
    }
}
