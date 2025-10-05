package com.example.bankpay.api.admin;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.OpenAccountRequest;
import com.example.bankpay.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/accounts")
public class AdminAccountsController {

    private final AccountService service;

    public AdminAccountsController(AccountService service) {
        this.service = service;
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
}
