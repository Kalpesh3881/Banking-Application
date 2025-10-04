package com.example.bankpay.api.advice;

import com.example.bankpay.domain.exception.DomainException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomain(DomainException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("error", ex.code().name());
        body.put("message", ex.getMessage());
        if (!ex.metadata().isEmpty()) body.put("meta", ex.metadata());
        HttpStatus status = switch (ex.code()) {
            case DUPLICATE_EMAIL -> HttpStatus.CONFLICT;
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case OTP_INVALID, OTP_EXPIRED -> HttpStatus.UNPROCESSABLE_ENTITY;
            case RATE_LIMITED -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalid(MethodArgumentNotValidException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("error", "VALIDATION_FAILED");
        body.put("message", "Request validation failed");
        Map<String,String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex) {
        Map<String,Object> body = new HashMap<>();
        body.put("error", "VALIDATION_FAILED");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}

