package com.grababite.backend.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore

@Entity
@Table(name = "menu_items")
public class MenuItem extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id") // This is the primary key
    private UUID menuItemId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "image_url")
    private String imageUrl;

    // A MenuItem belongs to one Cafeteria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id", nullable = false) // This is the correct foreign key
    @JsonIgnore // Ignore this field during serialization to prevent proxy issues
    private Cafeteria cafeteria;

    // Optional relationship to StandardMenuItem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_menu_item_id") // Foreign key to standard_menu_items table
    @JsonIgnore // Ignore this field during serialization
    private StandardMenuItem standardMenuItem;

    // Getters and Setters
    public UUID getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
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

    // IMPORTANT: If you need cafeteria details in the response, you'll need to
    // either eagerly fetch it or use a DTO to manually map relevant fields.
    // For now, it's ignored.
    public Cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    public StandardMenuItem getStandardMenuItem() {
        return standardMenuItem;
    }

    public void setStandardMenuItem(StandardMenuItem standardMenuItem) {
        this.standardMenuItem = standardMenuItem;
    }
}
