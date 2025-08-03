package com.grababite.backend.dto;

import java.util.UUID;

public class OnboardingRequest {

    private String name;
    private String email;
    private String authId;
    private String role;
    private UUID collegeId;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(UUID collegeId) {
        this.collegeId = collegeId;
    }
}