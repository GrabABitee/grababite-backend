package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cafeterias")
public class Cafeteria extends AuditModel {

    // cafeteria_id is the actual primary key in the database and should be UUID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Or GenerationType.UUID if you want DB to generate UUIDs
    @Column(name = "cafeteria_id") // Explicitly map to the existing cafeteria_id column
    private UUID cafeteriaId; // Changed type to UUID and made it the @Id

    // Removed the 'id' field as it's redundant with cafeteriaId being the PK

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "is_open")
    private Boolean isOpen;

    // Getters and Setters
    public UUID getCafeteriaId() { // Renamed getter to match field
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) { // Renamed setter to match field
        this.cafeteriaId = cafeteriaId;
    }

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
}
