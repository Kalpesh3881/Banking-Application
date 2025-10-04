package com.example.bankpay.service.port;

import com.example.bankpay.domain.model.User;
import java.util.Optional;

public interface UserGateway {
    boolean existsByEmail(String email);
    User create(User user, String firstName, String lastName);
    Optional<User> findById(Long userId);
    void enableUser(Long userId);
}

