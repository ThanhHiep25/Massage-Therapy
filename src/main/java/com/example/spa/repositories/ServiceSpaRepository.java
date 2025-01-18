package com.example.spa.repositories;

import com.example.spa.entities.ServiceSpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceSpaRepository extends JpaRepository<ServiceSpa, Long> {
}
