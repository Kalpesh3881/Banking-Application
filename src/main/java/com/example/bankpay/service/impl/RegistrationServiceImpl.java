package com.example.bankpay.service.impl;


import com.example.bankpay.domain.dto.RegisterRequest;
import com.example.bankpay.domain.dto.RegisterResponse;
import com.example.bankpay.domain.dto.RequestOtpRequest;
import com.example.bankpay.domain.enums.OtpPurpose;
import com.example.bankpay.domain.event.UserRegisteredEvent;
import com.example.bankpay.domain.exception.DomainException;
import com.example.bankpay.domain.model.User;
import com.example.bankpay.service.OtpService;
import com.example.bankpay.service.RegistrationService;
import com.example.bankpay.service.port.EventPublisher;
import com.example.bankpay.service.port.UserGateway;
import com.example.bankpay.support.ClockProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserGateway users;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher events;
    private final ClockProvider clock;
    private final OtpService otpService;

    public RegistrationServiceImpl(UserGateway users,
                                   PasswordEncoder passwordEncoder,
                                   EventPublisher events,
                                   ClockProvider clock,
                                   OtpService otpService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.events = events;
        this.clock = clock;
        this.otpService = otpService;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw DomainException.duplicateEmail(email);
        }

        Instant now = clock.now();
        String hash = passwordEncoder.encode(request.password());

        User newUser = User.newForRegistration(email, request.phone(), hash, now);
        User saved = users.create(newUser, request.firstName(), request.lastName());

        // Publish event for welcome/KYC/analytics
        events.publish(UserRegisteredEvent.of(saved.id(), saved.email(), saved.phone(), now));

        // Trigger OTP (purpose REGISTER). We ignore response here; controller has separate endpoint too.
        otpService.issueOtp(new RequestOtpRequest(saved.id(), OtpPurpose.REGISTER));

        return RegisterResponse.pending(saved.id());
    }
}
