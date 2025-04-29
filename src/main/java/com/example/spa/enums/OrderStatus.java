package com.example.spa.enums;

public enum OrderStatus {
    PENDING,      // Đơn hàng mới được tạo
    PROCESSING,   // Đang xử lý
    SHIPPED,      // Đã giao cho đơn vị vận chuyển
    DELIVERED,    // Đã giao thành công
    CANCELLED,    // Đã hủy
    REFUNDED      // Đã hoàn tiền
}