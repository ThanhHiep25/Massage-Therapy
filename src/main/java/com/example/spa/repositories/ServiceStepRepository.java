package com.example.spa.repositories;

import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceStepRepository extends JpaRepository<ServiceStep, Long> {
    List<ServiceStep> findByServiceSpa(ServiceSpa existingService);
    void deleteByServiceSpa(ServiceSpa serviceSpa);
}
