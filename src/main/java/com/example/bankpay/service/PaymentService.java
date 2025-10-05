package com.example.bankpay.service;

import com.example.bankpay.domain.dto.InternalTransferRequest;
import com.example.bankpay.domain.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse internalTransfer(InternalTransferRequest request, String idempotencyKey);
}
