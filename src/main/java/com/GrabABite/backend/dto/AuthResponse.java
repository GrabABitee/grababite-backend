package com.grababite.backend.dto;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private String role;
    private UUID userId;

    public AuthResponse(String token, String role, UUID userId) {
        this.token = token;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public UUID getUserId() {
        return userId;
    }
}
