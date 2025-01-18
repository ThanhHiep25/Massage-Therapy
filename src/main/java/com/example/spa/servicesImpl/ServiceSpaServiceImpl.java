package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.repositories.ServiceSpaRepository;
import com.example.spa.repositories.ServiceStepRepository;
import com.example.spa.services.ServiceSpaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true )
@RequiredArgsConstructor
public class ServiceSpaServiceImpl implements ServiceSpaService {

   // @Autowired
    private ServiceSpaRepository serviceSpaRepository;

   // @Autowired
    private ServiceStepRepository serviceStepRepository;


    @Override
    public ServiceSpa createServiceSpa(ServiceSpa serviceSpa, List<ServiceStep> steps) {
        ServiceSpa savedService = serviceSpaRepository.save(serviceSpa);  // Lưu serviceSpa lần đầu
        for (ServiceStep step : steps) {
            step.setServiceSpa(savedService);  // Liên kết ServiceStep với ServiceSpa
            serviceStepRepository.save(step);  // Lưu ServiceStep
        }
        return savedService;  // Trả về ServiceSpa đã lưu
    }



//    @Override
//    public ServiceSpa createServiceSpa(ServiceSpaRequest request) {
//        var serviceSPA = serviceSpaMapper.toServiceSpa(request);
//        return  serviceSpaRepository.save(serviceSPA);
//    }

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
            throw new RuntimeException("ServiceSpa with id " + id + " not found");
        }
        serviceSpaRepository.deleteById(id);
    }

    @Override
    public ServiceSpa updateServiceSpa(Long id, ServiceSpa serviceSpa, List<ServiceStep> steps) {
        ServiceSpa existingService = serviceSpaRepository.findById(id).orElseThrow(() ->
                new RuntimeException("ServiceSpa with id " + id + " not found"));

        existingService.setName(serviceSpa.getName());
        existingService.setDescription(serviceSpa.getDescription());
        existingService.setPrice(serviceSpa.getPrice());
        existingService.setDuration(serviceSpa.getDuration());
        existingService.setCategories(serviceSpa.getCategories());
        existingService.setImageUrl(serviceSpa.getImageUrl());
        existingService.setService_type(serviceSpa.getService_type());

        // Update steps
        serviceStepRepository.deleteAll(existingService.getSteps());
        for (ServiceStep step : steps) {
            step.setServiceSpa(existingService);
            serviceStepRepository.save(step);
        }

        return serviceSpaRepository.save(existingService);
    }


}