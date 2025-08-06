package com.grababite.backend.services;

import com.grababite.backend.dto.OrderCreationRequest;
import com.grababite.backend.dto.OrderItemRequest;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.Order;
import com.grababite.backend.models.OrderItem;
import com.grababite.backend.models.MenuItem;
import com.grababite.backend.models.User;
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.repositories.MenuItemRepository;
import com.grababite.backend.repositories.OrderItemRepository;
import com.grababite.backend.repositories.OrderRepository;
import com.grababite.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Random;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory


@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class); // Initialize Logger

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CafeteriaRepository cafeteriaRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all orders.
     * This method is typically for administrative access.
     * @return A list of all Order objects.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     * @param id The UUID of the order.
     * @return An Optional containing the Order if found.
     */
    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    /**
     * Retrieves orders associated with a specific cafeteria.
     * This is intended for cafeteria owners to view orders for their cafeteria.
     * @param cafeteriaId The UUID of the cafeteria.
     * @return A list of Order objects for the given cafeteria.
     */
    public List<Order> getOrdersByCafeteriaId(UUID cafeteriaId) {
        return orderRepository.findByCafeteriaCafeteriaId(cafeteriaId);
    }

    /**
     * Retrieves orders placed by a specific user.
     * This is intended for regular users to view their own orders.
     * @param userId The UUID of the user.
     * @return A list of Order objects placed by the given user.
     */
    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUser_Id(userId);
    }

    /**
     * Creates a new order and its associated order items.
     * This method is transactional to ensure atomicity of order creation.
     * @param request The OrderCreationRequest DTO containing order details and items.
     * @return The created Order object.
     * @throws ResourceNotFoundException if cafeteria or any menu item is not found.
     * @throws IllegalArgumentException if an order item has a non-positive quantity.
     */
    @Transactional
    public Order createOrder(OrderCreationRequest request) {
        // 1. Validate Cafeteria
        Cafeteria cafeteria = cafeteriaRepository.findById(request.getCafeteriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cafeteria not found with ID: " + request.getCafeteriaId()));
        logger.debug("Found Cafeteria: {}", cafeteria.getName());

        // 2. Validate and set User
        User user = null;
        if (request.getUserId() != null) {
            logger.debug("Attempting to find user with ID: {}", request.getUserId());
            Optional<User> userOptional = userRepository.findById(request.getUserId());
            if (userOptional.isPresent()) {
                user = userOptional.get();
                logger.debug("Found User: {} ({})", user.getEmail(), user.getId());
            } else {
                logger.error("User not found with ID: {}. Throwing ResourceNotFoundException.", request.getUserId());
                throw new ResourceNotFoundException("User not found with ID: " + request.getUserId());
            }
        } else {
            // This block should ideally not be reached if user_id is NOT NULL in DB and entity.
            // If it is reached, it means userId in request was null, which would then cause the DataIntegrityViolationException.
            logger.warn("OrderCreationRequest received with null userId. This might cause issues if Order.user is non-nullable.");
        }

        // 3. Create Order entity
        Order order = new Order();
        order.setCafeteria(cafeteria);
        order.setUser(user); // Set the user who placed the order
        order.setStatus("PENDING"); // Initial status
        order.setPickupCode(generateUniquePickupCode()); // Generate a unique pickup code
        logger.debug("Generated pickup code: {}", order.getPickupCode());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Process Order Items
        for (OrderItemRequest itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu Item not found with ID: " + itemRequest.getMenuItemId()));
            logger.debug("Found MenuItem: {} (Price: {})", menuItem.getName(), menuItem.getPrice());

            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity for menu item " + menuItem.getName() + " must be positive.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Link to the current order
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());

            // Calculate item total and add to overall total
            totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            order.addOrderItem(orderItem); // Add to the order's collection
        }

        order.setTotalAmount(totalAmount);
        logger.debug("Calculated total amount: {}", totalAmount);

        // 5. Save Order (this will cascade save OrderItems if configured correctly in Order entity)
        logger.debug("Attempting to save order...");
        Order savedOrder = orderRepository.save(order);
        logger.debug("Order saved successfully with ID: {}", savedOrder.getOrderId());

        return savedOrder;
    }

    /**
     * Updates the status of an existing order.
     * @param id The UUID of the order to update.
     * @param newStatus The new status string.
     * @return The updated Order object, or null if not found.
     * @throws IllegalArgumentException if the new status is invalid.
     */
    public Order updateOrderStatus(UUID id, String newStatus) {
        return orderRepository.findById(id).map(order -> {
            // Basic validation for status (you might have an enum or more complex logic)
            if (!isValidOrderStatus(newStatus)) {
                throw new IllegalArgumentException("Invalid order status: " + newStatus);
            }
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }).orElse(null);
    }

    /**
     * Deletes an order by its ID.
     * @param id The UUID of the order to delete.
     * @return true if the order was deleted, false otherwise.
     */
    public boolean deleteOrder(UUID id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Generates a unique 6-digit numeric pickup code.
     * This method ensures the generated code is unique by checking against existing codes in the database.
     */
    private String generateUniquePickupCode() {
        Random random = new Random();
        String code;
        do {
            // Generate a 6-digit number (between 100000 and 999999)
            int randomNumber = 100000 + random.nextInt(900000);
            code = String.valueOf(randomNumber);
        } while (orderRepository.findByPickupCode(code).isPresent()); // Keep generating until unique
        return code;
    }

    private boolean isValidOrderStatus(String status) {
        // Define your valid order statuses here
        return List.of("PENDING", "PREPARING", "READY_FOR_PICKUP", "COMPLETED", "CANCELLED")
                   .contains(status.toUpperCase());
    }
}
