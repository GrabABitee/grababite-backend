package com.grababite.backend.repositories;

import com.grababite.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// @Repository marks this interface as a Spring Data JPA repository.
// It provides standard CRUD operations for the OrderItem entity,
// with OrderItem as the entity type and UUID as the type of its primary key.
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    // Spring Data JPA automatically provides methods like save(), findById(), findAll(), deleteById().
    // You can add custom query methods here if needed.
}
