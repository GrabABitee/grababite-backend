package com.grababite.backend.dto;

import com.grababite.backend.models.User;

public class AuthResponse {

    private String message;
    private User user;

    public AuthResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    // Getters and Setters (omitted for brevity)
}