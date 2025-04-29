package com.example.spa.services;

import com.example.spa.dto.request.CreateOrderRequest;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.entities.User;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest createOrderRequest);
    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByUserId(Long userId);

    List<OrderResponse> getOrdersByUser(User user);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);
}