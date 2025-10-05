package com.example.bankpay.api;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.AccountsListResponse;
import com.example.bankpay.service.AccountService;
import com.example.bankpay.service.StatementService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountService service;
    private final StatementService statements;

    public AccountsController(AccountService service, StatementService statements) {
        this.service = service;
        this.statements = statements;
    }

    /** Get a single account by id. */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@PathVariable("id") Long id) {
         return ResponseEntity.ok(service.getById(id));
    }

    /**
     * List accounts for a customer.
     * (In a real app you'd infer customerId from auth; for now we pass it explicitly.)
     */
    @GetMapping
    public ResponseEntity<AccountsListResponse> listByCustomer(@RequestParam("customerId") @NotNull Long customerId) {
        return ResponseEntity.ok(service.listByCustomer(customerId));
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<?> statement(
            @PathVariable("id") Long id,
            @RequestParam("from") String fromDate,
            @RequestParam("to") String toDate,
            @RequestParam(value = "format", required = false, defaultValue = "JSON") String format
    ) {
        // inclusive range: [from 00:00Z, to 23:59:59.999Z] -> implemented via toExclusive = to + 1 day - 1 ns by using inclusive in gateway
        Instant from = LocalDate.parse(fromDate).atStartOfDay().toInstant(ZoneOffset.UTC);
        // treat 'to' inclusive by adding end-of-day
        Instant to = LocalDate.parse(toDate).plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusNanos(1);

        var stmt = statements.generate(id, from, to);

        if ("CSV".equalsIgnoreCase(format)) {
            byte[] csv = statements.exportCsv(stmt);
            String filename = "statement_%d_%s_%s.csv".formatted(id, fromDate, toDate);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(csv);
        }
        return ResponseEntity.ok(stmt);
    }
}
