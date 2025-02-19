
package com.example.spa.repositories;

import com.example.spa.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;



@Repository

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
