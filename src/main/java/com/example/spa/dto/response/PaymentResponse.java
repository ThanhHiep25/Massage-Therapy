package com.example.spa.dto.response;

import com.example.spa.entities.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private String paymentMethod;
    private AppointmentResponse appointment;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private LocalDateTime transactionTime;
    private String bankCode;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public PaymentResponse(Payment payment) {
        this.id = payment.getPaymentId();
        this.paymentMethod = payment.getPaymentMethod();
        this.appointment = new AppointmentResponse(payment.getAppointment());
        this.transactionId = payment.getTransactionId();
        this.amount = payment.getAmount();
        this.paymentDate = payment.getPaymentDate();
        this.transactionTime = payment.getTransactionTime();
        this.bankCode = payment.getBankCode();
        this.status = payment.getStatus().name();
        this.note = payment.getNote();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }
}
