package com.grababite.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

// DTO for summarizing popular menu items, typically for reporting.
public class PopularMenuItemResponse {
    private UUID menuItemId;
    private String menuItemName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenueGenerated;
    private UUID cafeteriaId; // Optional: if reporting popular items per cafeteria
    private String cafeteriaName; // Optional: if reporting popular items per cafeteria

    // Constructors
    public PopularMenuItemResponse() {
    }

    public PopularMenuItemResponse(UUID menuItemId, String menuItemName, Long totalQuantitySold, BigDecimal totalRevenueGenerated, UUID cafeteriaId, String cafeteriaName) {
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenueGenerated = totalRevenueGenerated;
        this.cafeteriaId = cafeteriaId;
        this.cafeteriaName = cafeteriaName;
    }

    // Getters and Setters
    public UUID getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(UUID menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public Long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public BigDecimal getTotalRevenueGenerated() {
        return totalRevenueGenerated;
    }

    public void setTotalRevenueGenerated(BigDecimal totalRevenueGenerated) {
        this.totalRevenueGenerated = totalRevenueGenerated;
    }

    public UUID getCafeteriaId() {
        return cafeteriaId;
    }

    public void setCafeteriaId(UUID cafeteriaId) {
        this.cafeteriaId = cafeteriaId;
    }

    public String getCafeteriaName() {
        return cafeteriaName;
    }

    public void setCafeteriaName(String cafeteriaName) {
        this.cafeteriaName = cafeteriaName;
    }
}
