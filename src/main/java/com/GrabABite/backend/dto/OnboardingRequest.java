package com.grababite.backend.dto;

import java.util.UUID; // Import UUID for collegeId

// This DTO is used for the initial user onboarding, typically
// for users coming from external authentication (like Google via authId).
public class OnboardingRequest {
    private String authId;
    private String name;
    private String email;
    private String password;
    private String role; // The role string (e.g., "STUDENT", "FACULTY")
    private UUID collegeId; // Optional: to link the user to a college during onboarding

    // Getters and Setters
    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

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
    public String getPassword() {
        return password;
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
