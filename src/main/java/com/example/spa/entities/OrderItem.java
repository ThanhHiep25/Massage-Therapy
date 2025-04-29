package com.example.spa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price; // Giá tại thời điểm đặt hàng

    @Column(name = "sub_total", nullable = false)
    private BigDecimal subTotal;

    // Helper method to calculate subtotal
    @PrePersist
    @PreUpdate
    private void calculateSubTotal() {
        this.subTotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}