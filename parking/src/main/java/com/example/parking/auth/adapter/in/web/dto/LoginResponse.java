package com.example.parking.auth.adapter.in.web.dto;

import com.example.parking.auth.domain.model.Role;

public class LoginResponse {

    private final Long id;
    private final String username;
    private final Role role;
    private final String tokenType;
    private final String accessToken;

    public LoginResponse(
            Long id,
            String username,
            Role role,
            String tokenType,
            String accessToken
    ) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }
}