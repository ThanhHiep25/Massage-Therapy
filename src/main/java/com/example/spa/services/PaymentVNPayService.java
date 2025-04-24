package com.example.spa.services;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.entities.Payment;


public interface PaymentVNPayService {

	Payment createPayment(AppointmentResponse appointment, Long amount, String paymentMethod, String status);

	String createVNPayPayment(AppointmentResponse appointment, Long amount);

	PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException;

	Optional<Payment> findById(Long id);


    // Thống kê thổng số lần thanh toán
    long countPayments();

	BigDecimal getSumAmount();

	List<Map<String, Object>> getMonthlyPaymentRevenue();
}
