package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.repositories.ServiceSpaRepository;
import com.example.spa.repositories.ServiceStepRepository;
import com.example.spa.services.ServiceSpaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceSpaServiceImpl implements ServiceSpaService {

    @Autowired
    private ServiceSpaRepository serviceSpaRepository;

    @Autowired
    private ServiceStepRepository serviceStepRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public ServiceSpa createServiceSpa(ServiceSpa serviceSpa, List<ServiceStep> steps) {
        ServiceSpa savedService = serviceSpaRepository.save(serviceSpa);
        for (ServiceStep step : steps) {
            step.setServiceSpa(savedService);
            serviceStepRepository.save(step);
        }
        return savedService;
    }

    @Override
    public ServiceSpa getServiceSpaById(Long id) {
        return serviceSpaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ServiceSpa with id " + id + " not found"));
    }

    @Override
    public List<ServiceSpa> getAllServiceSpas() {
        return serviceSpaRepository.findAll();
    }

    @Override
    public void deleteServiceSpa(Long id) {
        if (!serviceSpaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ServiceSpa with id " + id + " not found");
        }
        serviceSpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ServiceSpa updateServiceSpa(Long id, ServiceSpa serviceSpa, List<ServiceStep> steps) {
        ServiceSpa existingService = getServiceSpaById(id);

        existingService.setName(serviceSpa.getName());
        existingService.setDescription(serviceSpa.getDescription());
        existingService.setPrice(serviceSpa.getPrice());
        existingService.setDuration(serviceSpa.getDuration());
        existingService.setCategories(serviceSpa.getCategories());
        existingService.setImageUrl(serviceSpa.getImageUrl());
        existingService.setService_type(serviceSpa.getService_type());

        List<ServiceStep> existingSteps = serviceStepRepository.findByServiceSpa(existingService);
        for (ServiceStep step : steps) {
            step.setServiceSpa(existingService);
            if (!existingSteps.contains(step)) {
                serviceStepRepository.save(step);
            }
        }

        return serviceSpaRepository.save(existingService);
    }

    @Override
    @Transactional
    public List<String> importServiceSpas(List<ServiceSpaRequest> requests) {
        List<String> errors = new ArrayList<>();
        for (ServiceSpaRequest request : requests) {
            try {
                ServiceSpa serviceSpa = request.toServiceSpa();
                createServiceSpa(serviceSpa, request.getSteps());
            } catch (Exception e) {
                errors.add("Lỗi khi import dịch vụ: " + request.getName() + " - " + e.getMessage());
            }
        }
        return errors;
    }

    @Override
    @Transactional
    public List<String> importServiceSpasFromFile(MultipartFile file) {
        try {
            List<ServiceSpaRequest> requests = objectMapper.readValue(file.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ServiceSpaRequest.class));
            return importServiceSpas(requests);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lỗi đọc file JSON: " + e.getMessage());
        }
    }
}
