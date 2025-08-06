package com.grababite.backend.dto;

import java.util.Set;
import java.util.UUID;

// This DTO is used to represent User details when sent as a response from the API.
// It explicitly includes associated college and cafeteria IDs and names for frontend consumption.
public class UserResponse {
    private UUID id;
    private String email;
    private String name;
    private Set<String> roles;
    private UUID collegeId; // Include college ID
    private String collegeName; // Include college name for display
    private UUID cafeteriaId; // Include cafeteria ID
    private String cafeteriaName; // Include cafeteria name for display

    // Constructors
    public UserResponse() {}

    public UserResponse(UUID id, String email, String name, Set<String> roles, UUID collegeId, String collegeName, UUID cafeteriaId, String cafeteriaName) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.cafeteriaId = cafeteriaId;
        this.cafeteriaName = cafeteriaName;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public UUID getCollegeId() { return collegeId; }
    public void setCollegeId(UUID collegeId) { this.collegeId = collegeId; }
    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }
    public UUID getCafeteriaId() { return cafeteriaId; }
    public void setCafeteriaId(UUID cafeteriaId) { this.cafeteriaId = cafeteriaId; } // FIXED: Renamed from setId to setCafeteriaId
    public String getCafeteriaName() { return cafeteriaName; }
    public void setCafeteriaName(String cafeteriaName) { this.cafeteriaName = cafeteriaName; }
}
