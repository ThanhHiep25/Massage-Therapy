package com.example.spa.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

import com.example.spa.dto.PaymentDTO;
import com.example.spa.entities.Payment;


public interface PaymentVNPayService {

	Payment createPayment(Long userId, Long amount, String paymentMethod, String status);

	String createVNPayPayment(Long userId, Long amount);

	PaymentDTO handleVPNPaymentCallback(Map<String, String> params) throws UnsupportedEncodingException;

	Optional<Payment> findById(Long id);

	
}
