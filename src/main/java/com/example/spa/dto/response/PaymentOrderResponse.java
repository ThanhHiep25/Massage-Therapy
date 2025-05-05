package com.example.spa.dto.response;


import com.example.spa.entities.PaymentOrder;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderResponse {
    private Long id;
    private String paymentMethod;
    private OrderResponse order;
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private LocalDateTime transactionTime;
    private String bankCode;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public PaymentOrderResponse(PaymentOrder paymentOrder) {
        this.id = paymentOrder.getPaymentId();
        this.paymentMethod = paymentOrder.getPaymentMethod();
        this.order = new OrderResponse(paymentOrder.getOrder());
        this.transactionId = paymentOrder.getTransactionId();
        this.amount = paymentOrder.getAmount();
        this.paymentDate = paymentOrder.getPaymentDate();
        this.transactionTime = paymentOrder.getTransactionTime();
        this.bankCode = paymentOrder.getBankCode();
        this.status = paymentOrder.getStatus().name();
        this.note = paymentOrder.getNote();
        this.createdAt = paymentOrder.getCreatedAt();
        this.updatedAt = paymentOrder.getUpdatedAt();
    }
}
