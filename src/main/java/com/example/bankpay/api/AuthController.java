package com.example.bankpay.api;


import com.example.bankpay.domain.dto.*;
import com.example.bankpay.service.OtpService;
import com.example.bankpay.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final OtpService otpService;

    public AuthController(RegistrationService registrationService, OtpService otpService) {
        this.registrationService = registrationService;
        this.otpService = otpService;
    }

    /** Create disabled user, issue OTP, return userId + pending status. */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registrationService.register(request);
        return ResponseEntity.ok(response);
    }

    /** Re-issue OTP for a purpose (defaults to REGISTER in service). */
    @PostMapping("/otp/request")
    public ResponseEntity<OtpIssueResponse> requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        OtpIssueResponse response = otpService.issueOtp(request);
        return ResponseEntity.ok(response);
    }

    /** Verify OTP; if OK, enable the user. */
    @PostMapping("/otp/verify")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse response = otpService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }
}
