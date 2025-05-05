package com.example.spa.dto.response;

import com.example.spa.entities.OrderItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private ProductResponse product;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subTotal;

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.product = new ProductResponse(orderItem.getProduct());
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
        this.subTotal = orderItem.getSubTotal();
    }
}