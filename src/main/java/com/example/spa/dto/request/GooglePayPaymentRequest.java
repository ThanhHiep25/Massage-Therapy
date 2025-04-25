package com.example.spa.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GooglePayPaymentRequest {

    @NotNull(message = "Appointment ID không được để trống")
    private Long appointmentId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Min(value = 1, message = "Số tiền thanh toán phải lớn hơn 0")
    private Long amount;

    @NotNull(message = "Payment Method Token không được để trống")
    private String paymentMethodToken;

    @NotNull(message = "Transaction ID từ Google Pay không được để trống")
    private String transactionId;
}
