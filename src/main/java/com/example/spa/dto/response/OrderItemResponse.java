package com.example.spa.dto.response;

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
}