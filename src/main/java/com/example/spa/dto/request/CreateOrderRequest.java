package com.example.spa.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderRequest {
    private Long userId;
    private String shippingAddress;
    private String shippingPhone;
    private LocalDateTime orderDate;
    private String notes;
    private List<OrderItemRequest> orderItems;

}