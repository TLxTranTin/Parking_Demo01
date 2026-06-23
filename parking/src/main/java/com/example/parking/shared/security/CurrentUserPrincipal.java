package com.example.parking.shared.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public class CurrentUserPrincipal implements AuthenticatedPrincipal {

    private final Long id;
    private final String username;
    private final String role;

    public CurrentUserPrincipal(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}