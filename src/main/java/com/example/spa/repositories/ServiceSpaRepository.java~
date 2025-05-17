package com.example.spa.repositories;

import com.example.spa.entities.ServiceSpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceSpaRepository extends JpaRepository<ServiceSpa, Long> {
    boolean existsByName(String name);
    ServiceSpa findByName(String name);
}
