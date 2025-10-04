package com.example.bankpay.support;


import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class ClockProvider {
    private final Clock clock = Clock.system(ZoneOffset.UTC);
    public Instant now() { return Instant.now(clock); }
    public Clock clock() { return clock; }
}
