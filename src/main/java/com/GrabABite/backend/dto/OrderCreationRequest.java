package com.grababite.backend.dto;

import java.util.List;
import java.util.UUID;

// DTO for creating a new Order, including its associated items and the user ID.
public class OrderCreationRequest {
    private UUID cafeteriaId; // The ID of the cafeteria where the order is placed
    private List<OrderItemRequest> orderItems; // A list of items in the order
    private UUID userId; // NEW: The ID of the user placing the order

    // Getters and Setters
    public UUID getCafeteriaId() {
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }

    // NEW: Getter and Setter for userId
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
