package com.example.spa.services;

import com.example.spa.dto.request.CreateOrderRequest;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.entities.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest createOrderRequest);

    // Update đơn hàng
    @Transactional
    OrderResponse updateOrder(Long id, CreateOrderRequest updateRequest);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByUserId(Long userId);

    List<OrderResponse> getOrdersByUser(User user);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);

    // Cập nhật trạng thái Processing
    void processOrder(Long id);

    // Cập nhật trạng thái SHIPPED
    void shippedOrder(Long id);

    // Cập nhật trạng thái DELIVERED
    void deliveredOrder(Long id);

    // Cập nhật trạng thái CANCELLED
    void cancelledOrder(Long id);

    // Cập nhật trạng thái PAID
    void paidOrder(Long id);

    // Cập nhật trạng thái REFUND
    void refundOrder(Long id);
}