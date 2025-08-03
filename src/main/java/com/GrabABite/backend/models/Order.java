        package com.grababite.backend.models;

        import jakarta.persistence.*;
        import java.math.BigDecimal;
        import java.util.UUID;

        @Entity
        @Table(name = "orders")
        public class Order extends AuditModel {

            @Id // This makes orderId the primary key
            @GeneratedValue(strategy = GenerationType.AUTO) // Or GenerationType.UUID
            @Column(name = "order_id", nullable = false, unique = true) // Map to existing order_id column
            private UUID orderId; // This is now the primary key

            // Removed the 'id' field as it's redundant with orderId being the PK

            @Column(name = "status", nullable = false)
            private String status;

            @Column(name = "total_amount", nullable = false)
            private BigDecimal totalAmount;

            @Column(name = "pickup_code", unique = true, nullable = false)
            private String pickupCode;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "cafeteria_id", nullable = false)
            private Cafeteria cafeteria;

            // Getters and Setters
            public UUID getOrderId() { // Renamed getter to match the @Id field
                return orderId;
            }

            public void setOrderId(UUID orderId) { // Renamed setter to match the @Id field
                this.orderId = orderId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public BigDecimal getTotalAmount() {
                return totalAmount;
            }

            public void setTotalAmount(BigDecimal totalAmount) {
                this.totalAmount = totalAmount;
            }

            public String getPickupCode() {
                return pickupCode;
            }

            public void setPickupCode(String pickupCode) {
                this.pickupCode = pickupCode;
            }

            public Cafeteria getCafeteria() {
                return cafeteria;
            }

            public void setCafeteria(Cafeteria cafeteria) {
                this.cafeteria = cafeteria;
            }
        }
        