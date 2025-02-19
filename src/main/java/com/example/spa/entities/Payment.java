package com.example.spa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

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
  private String status; // Example: "Pending", "Completed", "Failed"

  @Column(name = "payment_date", nullable = false)
  private LocalDateTime paymentDate; // Thời gian thanh toán

  @OneToOne
  @JoinColumn(name = "appointment_id", nullable = false)
  private Appointment appointment;

  @Column(name = "transaction_time", nullable = false)
  private LocalDateTime transactionTime;

  @Column(name = "bank_code")
  private String bankCode;

}