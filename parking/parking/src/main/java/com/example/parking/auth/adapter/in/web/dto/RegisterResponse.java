package com.example.parking.auth.adapter.in.web.dto;

import com.example.parking.auth.domain.model.Role;

public class RegisterResponse {

    private final Long id;
    private final String username;
    private final Role role;

    public RegisterResponse(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
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
}