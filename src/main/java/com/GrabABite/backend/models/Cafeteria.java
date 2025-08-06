package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore

@Entity
@Table(name = "cafeterias")
public class Cafeteria extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cafeteria_id")
    private UUID cafeteriaId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "is_open")
    private Boolean isOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    @JsonIgnore // Ignore this field during serialization to prevent proxy issues
    private College college;

    // Getters and Setters for cafeteriaId (matching the @Id field)
    public UUID getCafeteriaId() {
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    // Keep other getters and setters as they are
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

    // IMPORTANT: If you need college details in the response, you'll need to
    // either eagerly fetch it or use a DTO to manually map relevant fields.
    // For now, it's ignored.
    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }
}
