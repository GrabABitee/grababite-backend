package com.grababite.backend.services;

import com.grababite.backend.dto.PopularMenuItemResponse;
import com.grababite.backend.dto.SalesSummaryResponse;
import com.grababite.backend.models.Order;
import com.grababite.backend.models.OrderItem;
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.repositories.MenuItemRepository;
import com.grababite.backend.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CafeteriaRepository cafeteriaRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Generates a daily sales summary for a specific cafeteria.
     *
     * @param cafeteriaId The ID of the cafeteria.
     * @param date The date for which to generate the report.
     * @return SalesSummaryResponse containing aggregated sales data for the day.
     */
    public SalesSummaryResponse getDailySalesSummary(UUID cafeteriaId, LocalDate date) {
        // Define the start and end of the day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Fetch orders for the given cafeteria and date range that are completed
        List<Order> orders = orderRepository.findByCafeteriaCafeteriaId(cafeteriaId).stream()
                .filter(order -> order.getCreatedAt().isAfter(startOfDay) && order.getCreatedAt().isBefore(endOfDay))
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus())) // Only count completed orders
                .collect(Collectors.toList());

        BigDecimal totalSalesAmount = BigDecimal.ZERO;
        long totalItemsSold = 0L;

        for (Order order : orders) {
            totalSalesAmount = totalSalesAmount.add(order.getTotalAmount());
            for (OrderItem item : order.getOrderItems()) {
                totalItemsSold += item.getQuantity();
            }
        }

        String cafeteriaName = cafeteriaRepository.findById(cafeteriaId)
                .map(cafeteria -> cafeteria.getName())
                .orElse("Unknown Cafeteria");

        return new SalesSummaryResponse(
                cafeteriaId,
                cafeteriaName,
                date,
                totalSalesAmount,
                (long) orders.size(),
                totalItemsSold
        );
    }

    /**
     * Generates a list of popular menu items for a specific cafeteria within a date range.
     * Popularity is based on the total quantity sold.
     *
     * @param cafeteriaId The ID of the cafeteria.
     * @param startDate The start date of the reporting period.
     * @param endDate The end date of the reporting period.
     * @param limit The maximum number of popular items to return.
     * @return A list of PopularMenuItemResponse objects, sorted by quantity sold.
     */
    public List<PopularMenuItemResponse> getPopularMenuItems(UUID cafeteriaId, LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Fetch all completed orders for the given cafeteria within the date range
        List<Order> orders = orderRepository.findByCafeteriaCafeteriaId(cafeteriaId).stream()
                .filter(order -> order.getCreatedAt().isAfter(startDateTime) && order.getCreatedAt().isBefore(endDateTime))
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());

        // Aggregate order items to count total quantity and revenue per menu item
        Map<UUID, PopularMenuItemResponse> popularItemsMap = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getMenuItem().getMenuItemId(),
                        Collectors.reducing(
                                null, // Initial accumulator
                                orderItem -> {
                                    // Create a new PopularMenuItemResponse for each orderItem to aggregate
                                    return new PopularMenuItemResponse(
                                            orderItem.getMenuItem().getMenuItemId(),
                                            orderItem.getMenuItem().getName(),
                                            (long) orderItem.getQuantity(),
                                            orderItem.getMenuItem().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())),
                                            cafeteriaId,
                                            orderItem.getOrder().getCafeteria().getName()
                                    );
                                },
                                (acc, item) -> {
                                    // Merge function for combining PopularMenuItemResponse objects
                                    if (acc == null) return item; // First item for this menu item ID
                                    acc.setTotalQuantitySold(acc.getTotalQuantitySold() + item.getTotalQuantitySold());
                                    acc.setTotalRevenueGenerated(acc.getTotalRevenueGenerated().add(item.getTotalRevenueGenerated()));
                                    return acc;
                                }
                        )
                ));

        // Convert map values to list, sort by total quantity sold, and apply limit
        return popularItemsMap.values().stream()
                .sorted(Comparator.comparing(PopularMenuItemResponse::getTotalQuantitySold).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
