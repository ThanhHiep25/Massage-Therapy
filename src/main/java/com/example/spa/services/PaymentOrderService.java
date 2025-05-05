package com.example.spa.services;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.dto.response.PaymentOrderResponse;
import com.example.spa.dto.response.PaymentResponse;
import com.example.spa.entities.PaymentOrder;

import java.io.UnsupportedEncodingException;
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
}
