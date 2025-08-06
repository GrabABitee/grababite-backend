package com.grababite.backend.dto;

import java.util.UUID;

// DTO for creating a new Cafeteria, including the collegeId
public class CafeteriaCreationRequest {
    private String name;
    private String location;
    private Boolean isOpen;
    private UUID collegeId; // The ID of the college this cafeteria belongs to

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public UUID getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(UUID collegeId) {
        this.collegeId = collegeId;
    }
}
