package com.example.spa.repositories;

import com.example.spa.entities.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    DiscountCode findByCode(String code);
}
