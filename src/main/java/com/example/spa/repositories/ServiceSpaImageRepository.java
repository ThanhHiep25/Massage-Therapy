package com.example.spa.repositories;

import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceSpaImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceSpaImageRepository extends JpaRepository<ServiceSpaImage, Long> {
    List<ServiceSpaImage> findByServiceSpa_ServiceId(Long serviceId);
    void deleteByServiceSpa(ServiceSpa serviceSpa);
}
