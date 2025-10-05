package com.example.bankpay.api;

import com.example.bankpay.domain.dto.AccountResponse;
import com.example.bankpay.domain.dto.AccountsListResponse;
import com.example.bankpay.service.AccountService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final AccountService service;

    public AccountsController(AccountService service) {
        this.service = service;
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
}
