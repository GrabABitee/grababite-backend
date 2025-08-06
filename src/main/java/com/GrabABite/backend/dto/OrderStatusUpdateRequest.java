package com.grababite.backend.dto;

// DTO for updating the status of an order
public class OrderStatusUpdateRequest {
    private String status;

    // Getter and Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
