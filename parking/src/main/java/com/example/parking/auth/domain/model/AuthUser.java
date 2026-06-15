package com.example.parking.auth.domain.model;

public class AuthUser {

    private final Long id;
    private final String username;
    private final String passwordHash;
    private final Role role;

    public AuthUser(Long id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}