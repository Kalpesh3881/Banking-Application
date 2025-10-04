package com.example.bankpay.service;

import com.example.bankpay.domain.dto.OtpIssueResponse;
import com.example.bankpay.domain.dto.RequestOtpRequest;
import com.example.bankpay.domain.dto.VerifyOtpRequest;
import com.example.bankpay.domain.dto.VerifyOtpResponse;

public interface OtpService {
    OtpIssueResponse issueOtp(RequestOtpRequest request);
    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);
}
