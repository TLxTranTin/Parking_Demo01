package com.example.parking.auth.application.port.out;

import com.example.parking.auth.domain.model.AuthUser;

public interface IJwtTokenProviderPort {

    String generateToken(AuthUser user);

    boolean validateToken(String token);

    String extractUsername(String token);

    String extractRole(String token);

    Long extractUserId(String token);
}