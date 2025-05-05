package com.example.spa.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateOrderRequest {
    private Long userId;
    private String guestName;
    private String shippingAddress;
    private String shippingPhone;
    private LocalDateTime orderDate;
    private String notes;
    private List<OrderItemRequest> orderItems;

}