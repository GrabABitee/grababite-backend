package com.grababite.backend.controllers;

import com.grababite.backend.dto.OrderCreationRequest; // Correct DTO for creating orders
import com.grababite.backend.dto.OrderStatusUpdateRequest;
import com.grababite.backend.models.Order;
import com.grababite.backend.models.User;
import com.grababite.backend.services.OrderService;
import com.grababite.backend.services.UserService;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Retrieves orders based on the authenticated user's role.
     * - ADMIN: Retrieves all orders.
     * - CAFETERIA_OWNER: Retrieves orders for their associated cafeteria.
     * - STUDENT/FACULTY: Retrieves orders placed by themselves.
     * @return A list of Order objects relevant to the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Use getUserEntityByEmail to get the full User entity
        Optional<User> currentUserOptional = userService.getUserEntityByEmail(userEmail);
        if (currentUserOptional.isEmpty()) {
            logger.warn("Authenticated user {} not found in database.", userEmail);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = currentUserOptional.get();

        if (currentUser.getRoles().contains("ADMIN")) {
            logger.info("Admin user {} requesting all orders.", userEmail);
            return ResponseEntity.ok(orderService.getAllOrders());
        } else if (currentUser.getRoles().contains("CAFETERIA_OWNER")) {
            if (currentUser.getCafeteria() == null) {
                logger.warn("Cafeteria owner {} does not have an associated cafeteria.", userEmail);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            logger.info("Cafeteria owner {} requesting orders for cafeteria ID: {}", userEmail, currentUser.getCafeteria().getCafeteriaId());
            return ResponseEntity.ok(orderService.getOrdersByCafeteriaId(currentUser.getCafeteria().getCafeteriaId()));
        } else {
            logger.info("User {} requesting their own orders (ID: {}).", userEmail, currentUser.getId());
            return ResponseEntity.ok(orderService.getOrdersByUserId(currentUser.getId()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Use getUserEntityByEmail to get the full User entity
        Optional<User> currentUserOptional = userService.getUserEntityByEmail(userEmail);
        if (currentUserOptional.isEmpty()) {
            logger.warn("Authenticated user {} not found in database.", userEmail);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = currentUserOptional.get();

        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOptional.get();

        // Check authorization
        if (currentUser.getRoles().contains("ADMIN")) {
            logger.info("Admin user {} accessing order {}.", userEmail, id);
            return ResponseEntity.ok(order);
        } else if (currentUser.getRoles().contains("CAFETERIA_OWNER")) {
            if (currentUser.getCafeteria() != null && order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId())) {
                logger.info("Cafeteria owner {} accessing order {} for their cafeteria.", userEmail, id);
                return ResponseEntity.ok(order);
            }
        } else if (order.getUser() != null && order.getUser().getId().equals(currentUser.getId())) {
            logger.info("User {} accessing their own order {}.", userEmail, id);
            return ResponseEntity.ok(order);
        }

        logger.warn("User {} attempted to access unauthorized order {}.", userEmail, id);
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * POST /api/orders
     * Creates a new order.
     * - Only STUDENT role can create an order.
     * @param request The order creation request DTO.
     * @return The created order.
     */
    @PostMapping
    // @PreAuthorize("hasRole('STUDENT')") // Removed for now, handled by general security config
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest request) { // Ensure this is OrderCreationRequest
        logger.info("Received request to create order for cafeteriaId: {} by user ID: {}", request.getCafeteriaId(), request.getUserId());
        try {
            Order createdOrder = orderService.createOrder(request);
            logger.info("Order created successfully with ID: {}", createdOrder.getOrderId());
            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found when creating order: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument when creating order: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating order: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUT /api/orders/{id}/status
     * Updates the status of an existing order.
     * Access Control: Only ADMIN or CAFETERIA_OWNER (for their cafeteria's orders)
     * should be able to update order status.
     * @param id The UUID of the order to update.
     * @param request The OrderStatusUpdateRequest DTO containing the new status string.
     * @return ResponseEntity with the updated Order object and HTTP status 200 OK,
     * or 404 Not Found if the order does not exist, or 403 Forbidden if not authorized.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestBody OrderStatusUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Use getUserEntityByEmail to get the full User entity
        Optional<User> currentUserOptional = userService.getUserEntityByEmail(userEmail);
        if (currentUserOptional.isEmpty()) {
            logger.warn("Authenticated user {} not found in database during order status update.", userEmail);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = currentUserOptional.get();

        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOptional.get();

        // Authorization check
        boolean authorized = false;
        if (currentUser.getRoles().contains("ADMIN")) {
            authorized = true;
            logger.info("Admin user {} updating status for order {}.", userEmail, id);
        } else if (currentUser.getRoles().contains("CAFETERIA_OWNER")) {
            if (currentUser.getCafeteria() != null && order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId())) {
                authorized = true;
                logger.info("Cafeteria owner {} updating status for order {} in their cafeteria.", userEmail, id);
            }
        }

        if (!authorized) {
            logger.warn("User {} attempted to update status for unauthorized order {}.", userEmail, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument when updating order status for order {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while updating order status for order {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /api/orders/{id}
     * Deletes an order by its ID.
     * Access Control: Only ADMIN or CAFETERIA_OWNER (for their cafeteria's orders)
     * should be able to delete orders.
     * @param id The UUID of the order to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the order does not exist, or 403 Forbidden if not authorized.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Use getUserEntityByEmail to get the full User entity
        Optional<User> currentUserOptional = userService.getUserEntityByEmail(userEmail);
        if (currentUserOptional.isEmpty()) {
            logger.warn("Authenticated user {} not found in database during order deletion.", userEmail);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User currentUser = currentUserOptional.get();

        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOptional.get();

        // Authorization check
        boolean authorized = false;
        if (currentUser.getRoles().contains("ADMIN")) {
            authorized = true;
            logger.info("Admin user {} deleting order {}.", userEmail, id);
        } else if (currentUser.getRoles().contains("CAFETERIA_OWNER")) {
            if (currentUser.getCafeteria() != null && order.getCafeteria().getCafeteriaId().equals(currentUser.getCafeteria().getCafeteriaId())) {
                authorized = true;
                logger.info("Cafeteria owner {} deleting order {} in their cafeteria.", userEmail, id);
            }
        }

        if (!authorized) {
            logger.warn("User {} attempted to delete unauthorized order {}.", userEmail, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        boolean deleted = orderService.deleteOrder(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
