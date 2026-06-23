package com.example.parking.auth.adapter.in.web.dto;

import com.example.parking.auth.domain.model.Role;

public class AdminAccountResponse {

    private final Long id;
    private final String username;
    private final Role role;
    private final boolean enabled;

    public AdminAccountResponse(
            Long id,
            String username,
            Role role,
            boolean enabled
    ) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
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

    public boolean isEnabled() {
        return enabled;
    }
}