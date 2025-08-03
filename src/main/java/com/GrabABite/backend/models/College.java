package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "colleges")
public class College extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Or GenerationType.UUID if you want DB to generate UUIDs
    @Column(name = "college_id") // Explicitly map to the existing college_id column
    private UUID collegeId; // This is the primary key of the 'colleges' table

    @Column(name = "college_name", nullable = false)
    private String collegeName;

    @Column(name = "address")
    private String address;

    // Getters and Setters
    public UUID getCollegeId() { // Changed getter name to match field
        return collegeId;
    }

    public void setCollegeId(UUID collegeId) { // Changed setter name to match field
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
