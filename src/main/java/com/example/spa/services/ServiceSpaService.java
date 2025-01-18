package com.example.spa.services;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;

import java.util.List;

public interface ServiceSpaService {
    ServiceSpa createServiceSpa(ServiceSpa serviceSpa, List<ServiceStep> steps);
    //ServiceSpa createServiceSpa(ServiceSpaRequest request);
    ServiceSpa getServiceSpaById(Long id);
    List<ServiceSpa> getAllServiceSpas();
    void deleteServiceSpa(Long id);
    ServiceSpa updateServiceSpa(Long id, ServiceSpa serviceSpa, List<ServiceStep> steps);
}