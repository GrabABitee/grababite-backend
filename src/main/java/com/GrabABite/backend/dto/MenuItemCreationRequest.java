package com.grababite.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

// DTO for creating a new MenuItem.
// It can either link to a StandardMenuItem (via standardMenuItemId)
// or provide custom details (name, description, imageUrl).
public class MenuItemCreationRequest {
    private String name; // Optional: for custom items
    private String description; // Optional: for custom items
    private BigDecimal price; // Required
    private Boolean isAvailable; // Required
    private String imageUrl; // Optional: for custom items

    private UUID cafeteriaId; // Required: links to the cafeteria
    private UUID standardMenuItemId; // Optional: links to a standard menu item

    // Getters and Setters
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getCafeteriaId() {
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    // NEW: Getter and Setter for standardMenuItemId
    public UUID getStandardMenuItemId() {
        return standardMenuItemId;
    }

    public void setStandardMenuItemId(UUID standardMenuItemId) {
        this.standardMenuItemId = standardMenuItemId;
    }
}
