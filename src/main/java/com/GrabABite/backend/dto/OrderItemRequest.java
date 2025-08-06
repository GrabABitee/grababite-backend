package com.grababite.backend.dto;

import java.util.UUID;

// DTO for representing a single item within an OrderCreationRequest.
public class OrderItemRequest {
    private UUID menuItemId; // The ID of the menu item
    private Integer quantity; // The quantity of this menu item

    // Getters and Setters
    public UUID getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
