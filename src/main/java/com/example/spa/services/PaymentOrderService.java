package com.example.spa.services;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.dto.response.PaymentOrderResponse;
import com.example.spa.dto.response.PaymentResponse;
import com.example.spa.entities.Order;
import com.example.spa.entities.PaymentOrder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentOrderService {

    // Tạo thanh toán đơn hàng
    PaymentOrder createPayment(OrderResponse orderResponse, Long amount, String paymentMethod, String status);

    // Tạo URL thanh toán VNPay
    String createVNPayPayment(OrderResponse orderResponse, Long amount);

    // Xây dựng URL thanh toán VNPay
    PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException;

    List<PaymentOrderResponse> findAll();
    Optional<PaymentOrder> findById(Long id);

    // setStatus SUCCESS
    void setStatusSuccess(Long id);

    // Thống kê thổng số lần thanh toán
    long countPayments();

    // Thống kê thổng tiền thanh toán
    BigDecimal getSumAmount();

    // Thống kê thổng tiền thanh toán theo tháng
    List<Map<String, Object>> getMonthlyPaymentRevenue();

    PaymentOrder createCashPaymentForOrder(Order order);
}
