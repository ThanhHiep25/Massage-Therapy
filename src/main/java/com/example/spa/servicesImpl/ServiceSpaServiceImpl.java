package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.entities.Categories;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceSpaImage;
import com.example.spa.entities.ServiceStep;
import com.example.spa.enums.StatusBasic;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.CategoryRepository;
import com.example.spa.repositories.ServiceSpaImageRepository;
import com.example.spa.repositories.ServiceSpaRepository;
import com.example.spa.repositories.ServiceStepRepository;
import com.example.spa.services.ServiceSpaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceSpaServiceImpl implements ServiceSpaService {

    private final ServiceSpaRepository serviceSpaRepository;
    private final ServiceStepRepository serviceStepRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceSpaImageRepository serviceSpaImageRepository;
    private final ObjectMapper objectMapper;

//    @Override
//    @Transactional
//    public ServiceSpa createServiceSpa(ServiceSpaRequest serviceSpaRequest, List<ServiceStep> steps) {
//        Categories category = categoryRepository.findById(serviceSpaRequest.getCategoryId())
//                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
//
//        ServiceSpa newService = serviceSpaRequest.toServiceSpa(category);
//        ServiceSpa savedService = serviceSpaRepository.save(newService);
//
//        // Lưu danh sách ServiceStep
//        for (ServiceStep step : steps) {
//            step.setServiceSpa(savedService);
//        }
//        serviceStepRepository.saveAll(steps);
//
//        // Lưu danh sách hình ảnh
//        if (serviceSpaRequest.getImageUrls() != null && !serviceSpaRequest.getImageUrls().isEmpty()) {
//            List<ServiceSpaImage> images = serviceSpaRequest.getImageUrls().stream()
//                    .map(url -> ServiceSpaImage.builder()
//                            .imageUrl(url)
//                            .serviceSpa(savedService)
//                            .build())
//                    .toList();
//            serviceSpaImageRepository.saveAll(images);
//            savedService.setImages(images);
//        }
//
//        return savedService;
//    }

    @Override
    @Transactional
    public ServiceSpa createServiceSpa(ServiceSpaRequest serviceSpaRequest, List<ServiceStep> steps) {
        Categories category = categoryRepository.findById(serviceSpaRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        ServiceSpa newService = serviceSpaRequest.toServiceSpa(category);
        ServiceSpa savedService = serviceSpaRepository.save(newService);

        // ✅ Liên kết danh sách ServiceStep với ServiceSpa
        if (steps != null && !steps.isEmpty()) {
            steps.forEach(step -> step.setServiceSpa(savedService));
            savedService.getSteps().addAll(steps); // Gán steps vào ServiceSpa
            serviceStepRepository.saveAll(steps);
        }

        // ✅ Lưu danh sách hình ảnh nếu có
        if (serviceSpaRequest.getImageUrls() != null && !serviceSpaRequest.getImageUrls().isEmpty()) {
            List<ServiceSpaImage> images = serviceSpaRequest.getImageUrls().stream()
                    .map(url -> ServiceSpaImage.builder()
                            .imageUrl(url)
                            .serviceSpa(savedService)
                            .build())
                    .toList();
            serviceSpaImageRepository.saveAll(images);
            savedService.getImages().addAll(images); // Gán images vào ServiceSpa
        }

        return savedService;
    }


    @Override
    public ServiceSpa getServiceSpaById(Long id) {
        return serviceSpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ServiceSpa không tồn tại"));
    }

    @Override
    public List<ServiceSpa> getAllServiceSpas() {
        return serviceSpaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteServiceSpa(Long id) {
        if (!serviceSpaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ServiceSpa không tồn tại");
        }
        serviceSpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ServiceSpa updateServiceSpa(Long id, ServiceSpaRequest request, List<ServiceStep> steps) {
        ServiceSpa existingService = getServiceSpaById(id);

        if (request.getCategoryId() != null) {
            Categories category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existingService.setCategories(category);
        }

        existingService.setName(request.getName());
        existingService.setDescription(request.getDescription());
        existingService.setPrice(request.getPrice());
        existingService.setDuration(request.getDuration());
        existingService.setService_type(request.getServiceType());

        // Cập nhật danh sách ServiceStep
        List<ServiceStep> existingSteps = serviceStepRepository.findByServiceSpa(existingService);
        existingSteps.removeIf(step -> !steps.contains(step));
        serviceStepRepository.deleteAll(existingSteps);

        steps.forEach(step -> step.setServiceSpa(existingService));
        serviceStepRepository.saveAll(steps);

        // Cập nhật danh sách ảnh
        if (request.getImageUrls() != null) {
            // Xóa ảnh cũ trước khi thêm ảnh mới
            serviceSpaImageRepository.deleteByServiceSpa(existingService);

            List<ServiceSpaImage> newImages = request.getImageUrls().stream()
                    .map(url -> ServiceSpaImage.builder()
                            .imageUrl(url)
                            .serviceSpa(existingService)
                            .build())
                    .toList();

            serviceSpaImageRepository.saveAll(newImages);
            existingService.setImages(newImages);
        }

        return serviceSpaRepository.save(existingService);
    }


    @Override
    @Transactional
    public Map<String, Object> importServiceSpasFromFile(MultipartFile file) {
        try {
            // Đọc JSON từ file thành danh sách `ServiceSpaRequest`
            String jsonContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<ServiceSpaRequest> requests = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<List<ServiceSpaRequest>>() {}
            );

            // Gọi phương thức importServiceSpas để xử lý và lưu dữ liệu
            return importServiceSpas(requests);

        } catch (IOException e) {
            // Xử lý lỗi nếu file không hợp lệ
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Lỗi đọc file JSON: " + e.getMessage());
            return errorResponse;
        }
    }




    @Override
    @Transactional
    public Map<String, Object> importServiceSpas(List<ServiceSpaRequest> requests) {
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (ServiceSpaRequest request : requests) {
            try {
                Categories category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

                ServiceSpa newService = request.toServiceSpa(category);
                serviceSpaRepository.save(newService);

                successMessages.add("Thành công: " + request.getName());
            } catch (Exception e) {
                errorMessages.add("Lỗi: " + request.getName() + " - " + e.getMessage());
            }
        }

        // ✅ Trả về Map chứa cả thành công và lỗi
        Map<String, Object> response = new HashMap<>();
        response.put("status", errorMessages.isEmpty() ? "success" : "error");
        response.put("success", successMessages);
        response.put("errors", errorMessages);

        return response;
    }




    @Override
    public void deleteAllServiceSpas() {
        serviceSpaRepository.deleteAll();
    }

    @Override
    public ServiceSpa findByServiceName(String serviceName) {
        return serviceSpaRepository.findByName(serviceName);
    }

    @Override
    public boolean existsByServiceName(String serviceName) {
        return serviceSpaRepository.existsByName(serviceName);
    }

    @Override
    public void deactivateServiceSpa(Long id) {
        try {
            ServiceSpa serviceSpa = serviceSpaRepository.findById(id).orElseThrow(() ->
                    new AppException(ErrorCode.SERVICE_NOT_FOUND));
            serviceSpa.setStatus(StatusBasic.DEACTIVATED);
            serviceSpaRepository.save(serviceSpa);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_INVALID);
        }
    }

    @Override
    public void activateServiceSpa(Long id) {
        try {
            ServiceSpa serviceSpa = serviceSpaRepository.findById(id).orElseThrow(() ->
                    new AppException(ErrorCode.SERVICE_NOT_FOUND));
            serviceSpa.setStatus(StatusBasic.ACTIVATE);
            serviceSpaRepository.save(serviceSpa);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_INVALID);
        }
    }

}
