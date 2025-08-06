package com.grababite.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// DTO for summarizing sales data, typically for reporting.
public class SalesSummaryResponse {
    private UUID cafeteriaId;
    private String cafeteriaName;
    private LocalDate date; // Or a period like Month/Year
    private BigDecimal totalSalesAmount;
    private Long totalOrders;
    private Long totalItemsSold;

    // Constructors
    public SalesSummaryResponse() {
    }

    public SalesSummaryResponse(UUID cafeteriaId, String cafeteriaName, LocalDate date, BigDecimal totalSalesAmount, Long totalOrders, Long totalItemsSold) {
        this.cafeteriaId = cafeteriaId;
        this.cafeteriaName = cafeteriaName;
        this.date = date;
        this.totalSalesAmount = totalSalesAmount;
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
    }

    // Getters and Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalItemsSold() {
        return totalItemsSold;
    }

    public void setTotalItemsSold(Long totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }
}
