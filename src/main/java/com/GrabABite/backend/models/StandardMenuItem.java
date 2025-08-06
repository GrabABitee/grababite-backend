package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "standard_menu_items") // New table for global/standard menu items
public class StandardMenuItem extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "standard_menu_item_id")
    private UUID standardMenuItemId;

    @Column(name = "name", nullable = false, unique = true) // Name should be unique for standard items
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url") // URL for the standard item's image
    private String imageUrl;

    // Getters and Setters
    public UUID getStandardMenuItemId() {
        return standardMenuItemId;
    }

    public void setStandardMenuItemId(UUID standardMenuItemId) {
        this.standardMenuItemId = standardMenuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
