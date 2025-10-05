package com.example.bankpay.api.admin;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.DepositRequest;
import com.example.bankpay.domain.dto.DepositResponse;
import com.example.bankpay.domain.dto.OpenAccountRequest;
import com.example.bankpay.service.AccountService;
import com.example.bankpay.service.FundingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/accounts")
public class AdminAccountsController {

    private final AccountService service;
    private final FundingService funding;

    public AdminAccountsController(AccountService service, FundingService funding) {
        this.service = service;
        this.funding = funding;
    }

    /** Open a new account. */
    @PostMapping
    public ResponseEntity<AccountResponse> open(@Valid @RequestBody OpenAccountRequest request) {
        return ResponseEntity.ok(service.open(request));
    }

    /** Freeze an account. */
    @PatchMapping("/{id}/freeze")
    public ResponseEntity<AccountResponse> freeze(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.freeze(id));
    }

    /** Unfreeze an account. */
    @PatchMapping("/{id}/unfreeze")
    public ResponseEntity<AccountResponse> unfreeze(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.unfreeze(id));
    }

    /** Close an account. */
    @PatchMapping("/{id}/close")
    public ResponseEntity<AccountResponse> close(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.close(id));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable("id") Long accountId,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody DepositRequest request
    ) {
        return ResponseEntity.ok(funding.deposit(accountId, request, idempotencyKey));
    }
}
