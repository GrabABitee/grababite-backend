package com.grababite.backend.repositories;

import com.grababite.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Find an order by its unique pickup code
    Optional<Order> findByPickupCode(String pickupCode);

    // NEW: Find all orders for a specific cafeteria
    List<Order> findByCafeteriaCafeteriaId(UUID cafeteriaId);

    // NEW: Find all orders placed by a specific user
    // This assumes your Order entity has a 'user' field with a 'User' type
    List<Order> findByUser_Id(UUID userId);
}
