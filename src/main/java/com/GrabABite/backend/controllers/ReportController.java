package com.grababite.backend.controllers;

import com.grababite.backend.dto.PopularMenuItemResponse;
import com.grababite.backend.dto.SalesSummaryResponse;
import com.grababite.backend.services.ReportingService;
import com.grababite.backend.services.UserService; // Import UserService for security checks
import com.grababite.backend.exceptions.ResourceNotFoundException; // Import ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private UserService userService; // Autowire UserService for security checks

    /**
     * GET /api/reports/sales/daily
     * Generates a daily sales summary for a specific cafeteria.
     * Access Control: ADMIN or CAFETERIA_OWNER (for their own cafeteria).
     *
     * @param cafeteriaId The ID of the cafeteria for which to generate the report.
     * @param date The date for which to generate the report (e.g., "2025-08-01").
     * @return SalesSummaryResponse containing aggregated sales data for the day.
     */
    @GetMapping("/sales/daily")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CAFETERIA_OWNER') and @userService.getCurrentUserCafeteriaId() == #cafeteriaId)")
    public ResponseEntity<SalesSummaryResponse> getDailySalesSummary(
            @RequestParam UUID cafeteriaId,
            @RequestParam LocalDate date) {
        try {
            SalesSummaryResponse summary = reportingService.getDailySalesSummary(cafeteriaId, date);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            // Log the exception for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/reports/menu-items/popular
     * Generates a list of popular menu items for a specific cafeteria within a date range.
     * Access Control: ADMIN or CAFETERIA_OWNER (for their own cafeteria).
     *
     * @param cafeteriaId The ID of the cafeteria.
     * @param startDate The start date of the reporting period (e.g., "2025-07-01").
     * @param endDate The end date of the reporting period (e.g., "2025-07-31").
     * @param limit The maximum number of popular items to return.
     * @return A list of PopularMenuItemResponse objects, sorted by quantity sold.
     */
    @GetMapping("/menu-items/popular")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CAFETERIA_OWNER') and @userService.getCurrentUserCafeteriaId() == #cafeteriaId)")
    public ResponseEntity<List<PopularMenuItemResponse>> getPopularMenuItems(
            @RequestParam UUID cafeteriaId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit) { // Default limit to 5 if not provided
        try {
            List<PopularMenuItemResponse> popularItems = reportingService.getPopularMenuItems(cafeteriaId, startDate, endDate, limit);
            return ResponseEntity.ok(popularItems);
        } catch (Exception e) {
            // Log the exception for debugging
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
