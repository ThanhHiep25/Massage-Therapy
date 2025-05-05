package com.example.spa.enums;

public enum OrderStatus {
    PENDING,      // Đơn hàng mới được tạo
    PROCESSING,   // Đang xử lý
    SHIPPED,      // Đã giao cho đơn vị vận chuyển
    DELIVERED,    // Đã giao thành công
    CANCELLED,    // Đã hủy
    PAID,         // Đã thanh toán
    REFUND,       // Đã hoan tien

}