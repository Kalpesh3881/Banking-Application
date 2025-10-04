package com.example.bankpay.persistence.mongo;

import com.example.bankpay.persistence.mongo.doc.DbSequenceDoc;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class SequenceGenerator {

    private final MongoTemplate template;

    public SequenceGenerator(MongoTemplate template) {
        this.template = template;
    }

    public long next(String name) {
        Query q = new Query(Criteria.where("_id").is(name));
        Update u = new Update().inc("value", 1);
        FindAndModifyOptions opts = FindAndModifyOptions.options().upsert(true).returnNew(true);
        DbSequenceDoc seq = template.findAndModify(q, u, opts, DbSequenceDoc.class);
        return (seq == null) ? 1L : seq.value;
    }
}
