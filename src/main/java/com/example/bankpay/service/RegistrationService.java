package com.example.bankpay.service;

import com.example.bankpay.domain.dto.RegisterRequest;
import com.example.bankpay.domain.dto.RegisterResponse;

public interface RegistrationService {
    RegisterResponse register(RegisterRequest request);
}

