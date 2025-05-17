package com.example.spa.services;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ServiceSpaService {
    ServiceSpa createServiceSpa(ServiceSpaRequest serviceSpa, List<ServiceStep> steps);

    ServiceSpa getServiceSpaById(Long id);

    List<ServiceSpa> getAllServiceSpas();

    // Lấy danh sách ServiceSpa theo trạng thái ACTIVATE
    List<ServiceSpa> getAllActiveServiceSpas();

    void deleteServiceSpa(Long id);

    ServiceSpa updateServiceSpa(Long id, ServiceSpaRequest request, List<ServiceStep> steps);

    Map<String, Object> importServiceSpasFromFile(MultipartFile file);

    Map<String, Object> importServiceSpas(List<ServiceSpaRequest> requests);

    void deleteAllServiceSpas();

    ServiceSpa findByServiceName(String serviceName);

    boolean existsByServiceName(String serviceName);

    void deactivateServiceSpa(Long id);

    void activateServiceSpa(Long id);

    //Lấy tổng số lượng dịch vụ spa
    Long countServiceSpa();

    //Thống kê số lượng dịch vụ theo danh mục
    Map<String, Long> getServicesByCategory();

    //Thống kê số lượng dịch vụ theo danh mục (trả về name và value)
    List<Map.Entry<String, Long>> getServicesByCategoryWithNameValue();

    byte[] exportServiceSpasToExcel(List<ServiceSpaResponse> serviceSpas) throws IOException;
}
