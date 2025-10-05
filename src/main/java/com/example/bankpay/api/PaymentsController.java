package com.example.bankpay.api;

import com.example.bankpay.domain.dto.InternalTransferRequest;
import com.example.bankpay.domain.dto.PaymentResponse;
import com.example.bankpay.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

    private final PaymentService payments;

    public PaymentsController(PaymentService payments) {
        this.payments = payments;
    }

    /** Internal transfer between two accounts in the same bank.
     *  Use header: Idempotency-Key: <client-generated-uuid>
     */
    @PostMapping("/transfers/internal")
    public ResponseEntity<PaymentResponse> internalTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody InternalTransferRequest request) {
        return ResponseEntity.ok(payments.internalTransfer(request, idempotencyKey));
    }
}
