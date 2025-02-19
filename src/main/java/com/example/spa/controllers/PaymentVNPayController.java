package com.example.spa.controllers;

import com.example.spa.entities.Payment;
import com.example.spa.services.PaymentVNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentVNPayController {

	@Autowired
	private PaymentVNPayService paymentVNPayService;

	@PostMapping("/vnpay")
	@Operation(summary = "Create Thanh toán VNPAY", description = "Tạo thanh toán VNPAY",
			responses = {
					@ApiResponse(responseCode = "200", description = "Tạo thành công"),
					@ApiResponse(responseCode = "400", description = "Không hợp lệ")
			}
	)
	public ResponseEntity<?> createVNPayPayment(@Valid @RequestBody Payment payload) {
		try {
			Long userId = payload.getPaymentId();
			Long amount = payload.getPaymentId();

			String paymentUrl = paymentVNPayService.createVNPayPayment(userId, amount);

			if (paymentUrl != null && paymentUrl.startsWith("http")) {
				return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
			}

			return ResponseEntity.badRequest().body(new ErrorResponse("Failed to generate VNPay payment URL."));
		} catch (Exception e) {
			return ResponseEntity.status(500)
					.body(new ErrorResponse("Error during payment creation: " + e.getMessage()));
		}
	}

	@GetMapping("/vnpay/callback")
	@Operation(summary = "Xử lý callback từ VNPAY", description = "Xử lý callback từ VNPAY",
			responses = {
					@ApiResponse(responseCode = "200", description = "Xử lý thành công"),
					@ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
			}
	)
	public ResponseEntity<?> handleVNPayCallback(@RequestParam Map<String, String> params) {
		try {
			String responseCode = params.get("vnp_ResponseCode");
			String transactionStatus = params.get("vnp_TransactionStatus");
			String transactionNo = params.get("vnp_TransactionNo");
			String amount = params.get("vnp_Amount");
			String bankCode = params.get("vnp_BankCode");
			String orderInfor = params.get("vnp_OrderInfo");
			LocalDateTime transactionTime = params.get("vnp_PayDate").isEmpty() ? null
                    : LocalDateTime.parse(params.get("vnp_PayDate"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

			if ("00".equalsIgnoreCase(responseCode)) {
				// PaymentDTO success = paymentVNPayService.handleVPNPaymentCallback(params);
				// return ResponseEntity.ok(success);
				paymentVNPayService.handleVPNPaymentCallback(params);
				String redirectUrl = "http://localhost:5173/payment-success?" + "vnp_TransactionStatus="
						+ transactionStatus + "&" + "vnp_TransactionNo=" + transactionNo + "&" + "vnp_Amount=" + amount
						+ "&" + "vnp_BankCode=" + bankCode + "&" + "vnp_OrderInfo=" + orderInfor + "&" + "vnp_PayDate="
						+ transactionTime ;
				return ResponseEntity.status(302).header("Location", redirectUrl).build();

			} else {

				String redirectUrl = "http://localhost:5173/payment-success?" + "vnp_TransactionStatus="
						+ transactionStatus + "&" + "vnp_TransactionNo=" + transactionNo + "&" + "vnp_OrderInfo="
						+ orderInfor;
				return ResponseEntity.status(302).header("Location", redirectUrl).build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse("Internal Server Error: " + e.getMessage()));
		}
	}

	@GetMapping("/vnpay/transaction/{id}")
	@Operation(summary = "Lấy thông tin giao dịch theo ID", description = "Trả về thông tin giao dịch theo ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Tìm thành công"),
					@ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
			}
	)
	public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
		try {
			Optional<Payment> optional = paymentVNPayService.findById(id);

			if (optional.isPresent()) {
				return ResponseEntity.ok(optional.get());
			} else {
				return ResponseEntity.status(404).body(new ErrorResponse("Transaction not found with id: " + id));
			}
		} catch (Exception e) {
			return ResponseEntity.status(500)
					.body(new ErrorResponse("Error fetching transaction by id: " + e.getMessage()));
		}
	}

	@Getter
    public static class ErrorResponse {
		private String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

        public void setMessage(String message) {
			this.message = message;
		}
	}
}
