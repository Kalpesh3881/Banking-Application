package com.example.bankpay.service.impl;

import com.example.bankpay.domain.dto.AccountStatementLine;
import com.example.bankpay.domain.dto.AccountStatementResponse;
import com.example.bankpay.domain.exception.DomainException;
import com.example.bankpay.domain.model.Account;
import com.example.bankpay.domain.model.Transaction;
import com.example.bankpay.domain.enums.TransactionType;
import com.example.bankpay.service.StatementService;
import com.example.bankpay.service.port.AccountGateway;
import com.example.bankpay.service.port.TransactionGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.example.bankpay.domain.exception.ErrorCode.*;

@Service
public class StatementServiceImpl implements StatementService {

    private final AccountGateway accounts;
    private final TransactionGateway transactions;

    public StatementServiceImpl(AccountGateway accounts, TransactionGateway transactions) {
        this.accounts = accounts;
        this.transactions = transactions;
    }

    @Override
//    @Transactional(readOnly = true)
    public AccountStatementResponse generate(Long accountId, Instant fromInclusive, Instant toInclusive) {
        if (fromInclusive == null || toInclusive == null || toInclusive.isBefore(fromInclusive)) {
            throw new DomainException(STATEMENT_RANGE_INVALID, "Invalid statement range",
                    java.util.Map.of("from", fromInclusive, "to", toInclusive));
        }

        Account acc = accounts.findById(accountId).orElseThrow(() ->
                new DomainException(ACCOUNT_NOT_FOUND, "Account not found",
                        java.util.Map.of("accountId", accountId)));

        long opening = transactions.netChangeBefore(accountId, fromInclusive);
        List<Transaction> txns = transactions.findPostedBetween(accountId, fromInclusive, toInclusive);

        long running = opening;
        long totalCredits = 0L, totalDebits = 0L;
        List<AccountStatementLine> lines = new ArrayList<>(txns.size());

        for (Transaction t : txns) {
            long amt = t.amountMinor();
            if (t.type() == TransactionType.CREDIT) {
                running = Math.addExact(running, amt);
                totalCredits = Math.addExact(totalCredits, amt);
            } else {
                running = Math.subtractExact(running, amt);
                totalDebits = Math.addExact(totalDebits, amt);
            }
            lines.add(new AccountStatementLine(
                    t.id(), t.valueDate(), t.type().name(), t.amountMinor(), running,
                    t.reference(), t.counterparty(), t.correlationId()
            ));
        }

        long closing = running;

        return new AccountStatementResponse(
                acc.id(),
                acc.currency(),
                fromInclusive,
                toInclusive,
                opening,
                closing,
                totalCredits,
                totalDebits,
                lines
        );
    }

    @Override
    public byte[] exportCsv(AccountStatementResponse s) {
        StringBuilder sb = new StringBuilder(256 + s.lines().size() * 64);
        // header
        sb.append("accountId,currency,from,to,openingMinor,closingMinor,totalCreditsMinor,totalDebitsMinor\n");
        sb.append(s.accountId()).append(',')
                .append(s.currency()).append(',')
                .append(s.from()).append(',')
                .append(s.to()).append(',')
                .append(s.openingBalanceMinor()).append(',')
                .append(s.closingBalanceMinor()).append(',')
                .append(s.totalCreditsMinor()).append(',')
                .append(s.totalDebitsMinor()).append('\n');

        sb.append("\ntransactionId,valueDate,type,amountMinor,balanceAfterMinor,reference,counterparty,correlationId\n");
        for (var l : s.lines()) {
            sb.append(nullToEmpty(l.transactionId()))
                    .append(',').append(l.valueDate())
                    .append(',').append(l.type())
                    .append(',').append(l.amountMinor())
                    .append(',').append(l.balanceAfterMinor())
                    .append(',').append(csvEscape(l.reference()))
                    .append(',').append(csvEscape(l.counterparty()))
                    .append(',').append(csvEscape(l.correlationId()))
                    .append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        boolean needsQuote = s.contains(",") || s.contains("\"") || s.contains("\n");
        String v = s.replace("\"", "\"\"");
        return needsQuote ? "\"" + v + "\"" : v;
    }
    private static String nullToEmpty(Object o) { return o == null ? "" : o.toString(); }
}
