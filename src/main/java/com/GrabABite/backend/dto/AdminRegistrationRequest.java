package com.grababite.backend.dto;

// This class is a Data Transfer Object (DTO) used to capture
// the request body when registering a new admin user.
public class AdminRegistrationRequest {
    private String name;
    private String email;
    private String password; // Plain text password from the client (will be hashed)
    // Removed: private UUID collegeId; // Admin will NOT be linked to a college at creation

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Removed: Getter and Setter for collegeId
}
