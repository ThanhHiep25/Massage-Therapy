package com.example.spa.dto.response;

import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceSpaImage;
import com.example.spa.entities.ServiceStep;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSpaResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Long categoryId;
    private List<String> images; // Lưu danh sách URL ảnh thay vì đối tượng ServiceSpaImage
    private String serviceType;
    private List<ServiceStepDTO> steps;
    private String status;

    public ServiceSpaResponse(ServiceSpa serviceSpa, List<ServiceStep> steps) {
        this.id = serviceSpa.getServiceId();
        this.name = serviceSpa.getName();
        this.description = serviceSpa.getDescription();
        this.price = serviceSpa.getPrice();
        this.duration = serviceSpa.getDuration();
        this.categoryId = (serviceSpa.getCategories() != null) ? serviceSpa.getCategories().getCategoryId() : null;

        // Chuyển danh sách ServiceSpaImage thành danh sách URL ảnh
        this.images = serviceSpa.getImages().stream()
                .map(ServiceSpaImage::getImageUrl) // Chỉ lấy URL từ đối tượng ServiceSpaImage
                .collect(Collectors.toList());

        this.serviceType = serviceSpa.getService_type();
        this.steps = steps.stream()
                .map(ServiceStepDTO::new)
                .collect(Collectors.toList());
        this.status = serviceSpa.getStatus().name(); // Thêm trư��ng status vào DTO
    }

}

