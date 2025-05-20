package com.example.spa.services;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.PaymentResponse;
import com.example.spa.entities.Payment;


public interface PaymentVNPayService {

	Payment createPayment(AppointmentResponse appointment, Long amount, String paymentMethod, String status);

	Payment createPaymentGooglePay(AppointmentResponse appointmentResponse, Long amount, String paymentMethod);

	String createVNPayPayment(AppointmentResponse appointment, Long amount);

	PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException;

    // Lấy tất cả thanh toán
    List<PaymentResponse> findAll();

    // setStatus SUCCESS
	void setStatusSuccess(Long id);

	Optional<Payment> findById(Long id);


    // Thống kê thổng số lần thanh toán
    long countPayments();

	BigDecimal getSumAmount();

	List<Map<String, Object>> getMonthlyPaymentRevenue();
}
