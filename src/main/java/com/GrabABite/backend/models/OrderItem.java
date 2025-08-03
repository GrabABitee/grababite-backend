package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Primary key of the order_items table

    @Column(name = "order_item_id", nullable = false, unique = true) // Unique business identifier for the order item
    private UUID orderItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // --- NEW: Relationship to Order ---
    // An order item belongs to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // Foreign key to the 'orders' table
    private Order order;

    // --- NEW: Relationship to MenuItem ---
    // An order item is for one menu item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false) // Foreign key to the 'menu_items' table
    private MenuItem menuItem; // Assuming you have a MenuItem entity

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // New getters/setters for relationships
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}
