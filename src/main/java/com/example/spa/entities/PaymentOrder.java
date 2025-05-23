package com.example.spa.entities;

import com.example.spa.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments_order")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Example: "Cash", "Credit Card", "Online Payment"

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // Mã giao dịch từ cổng thanh toán (có thể null nếu thanh toán bằng tiền mặt)

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Số tiền thanh toán

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // Example: "Pending", "Completed", "Failed"

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate; // Thời gian thanh toán

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "is_deposit")
    private Boolean isDeposit = false; // True nếu là cọc, False nếu là thanh toán toàn phần

    @Column(name = "note")
    private String note; // Mô tả lý do thanh toán, có thể là "cọc", "trả góp", "trả đủ", v.v.

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
