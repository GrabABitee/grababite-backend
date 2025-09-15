package com.grababite.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grababite.backend.dto.OrderCreationRequest;
import com.grababite.backend.dto.OrderStatusUpdateRequest;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.models.Order;
import com.grababite.backend.models.User;
import com.grababite.backend.services.OrderService;
import com.grababite.backend.services.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User currentUser = userService.getUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        if (currentUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.ok(orderService.getAllOrders());
        } else if (currentUser.getRoles().contains("CAFETERIA_OWNER")) {
            if (currentUser.getCafeteria() == null) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(orderService.getOrdersByCafeteriaId(currentUser.getCafeteria().getCafeteriaId()));
        } else {
            return ResponseEntity.ok(orderService.getOrdersByUserId(currentUser.getId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User currentUser = userService.getUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (currentUser.getRoles().contains("ADMIN") ||
            (currentUser.getRoles().contains("CAFETERIA_OWNER") &&
             currentUser.getCafeteria() != null &&
             order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId())) ||
            (order.getUser() != null && order.getUser().getId().equals(currentUser.getId()))) {

            return ResponseEntity.ok(order);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest request) {
        try {
            Order createdOrder = orderService.createOrder(request);
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error creating order", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User currentUser = userService.getUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        boolean authorized = currentUser.getRoles().contains("ADMIN") ||
                (currentUser.getRoles().contains("CAFETERIA_OWNER") &&
                 currentUser.getCafeteria() != null &&
                 order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId()));

        if (!authorized) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid status update for order {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error updating order {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User currentUser = userService.getUserEntityByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        boolean authorized = currentUser.getRoles().contains("ADMIN") ||
                (currentUser.getRoles().contains("CAFETERIA_OWNER") &&
                 currentUser.getCafeteria() != null &&
                 order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId()));

        if (!authorized) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        boolean deleted = orderService.deleteOrder(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
