package com.example.spa.controllers;

import com.example.spa.dto.request.GooglePayPaymentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.entities.Payment;
import com.example.spa.enums.PaymentStatus;
import com.example.spa.services.AppointmentService;
import com.example.spa.services.PaymentVNPayService; // Import the VNPay service
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-pay")
public class GooglePayController {

    private final PaymentVNPayService paymentVNPayService; // Inject the VNPay service
    private final AppointmentService appointmentService;

    public GooglePayController(PaymentVNPayService paymentVNPayService, AppointmentService appointmentService) {
        this.paymentVNPayService = paymentVNPayService;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/save-payment-info")
    public ResponseEntity<?> saveGooglePayPaymentInfo(@RequestBody GooglePayPaymentRequest payload) {
        try {
            Long appointmentId = payload.getAppointmentId();
            AppointmentResponse appointmentResponse = appointmentService.getAppointmentById(appointmentId);

            if (appointmentResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Không tìm thấy lịch hẹn với ID: " + appointmentId));
            }

            // Reuse the createPayment method from PaymentVNPayService
            Payment payment = paymentVNPayService.createPaymentGooglePay(
                    appointmentResponse,
                    payload.getAmount(),
                    "GooglePay"// Set the payment method to "GooglePay"
            );

            payment.setTransactionId(payload.getTransactionId()); // Save Google Pay transaction ID
            // Set other relevant details from the payload if needed
            payment.setPaymentDate(java.time.LocalDateTime.now()); // Set the payment date
            payment.setIsDeposit(true); // Or based on your logic
            payment.setStatus(PaymentStatus.SUCCESS);

            // You might need to explicitly save the payment here if createPayment doesn't do it
            // (Looking at your createPayment, it does save the entity)

            return ResponseEntity.ok("Thông tin thanh toán Google Pay đã được lưu.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi khi lưu thông tin thanh toán Google Pay: " + e.getMessage()));
        }
    }

    record ErrorResponse(String message) {}
}