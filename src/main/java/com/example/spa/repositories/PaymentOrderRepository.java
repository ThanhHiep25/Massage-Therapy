package com.example.spa.repositories;

import com.example.spa.entities.PaymentOrder;
import com.example.spa.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByTransactionId(String vnpTxnRef);

    @Query("SELECT SUM(p.amount) FROM PaymentOrder p WHERE p.status = SUCCESS")
    BigDecimal sumAllAmountPayment();

    @Query("SELECT YEAR(p.paymentDate), MONTH(p.paymentDate), SUM(p.amount) " +
            "FROM PaymentOrder p " +
            "WHERE p.status = 'SUCCESS' " +
            "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate) " +
            "ORDER BY YEAR(p.paymentDate) ASC, MONTH(p.paymentDate) ASC")
    List<Object[]> sumPaymentAmountByMonth();

    long countByStatus(PaymentStatus paymentStatus);
}
