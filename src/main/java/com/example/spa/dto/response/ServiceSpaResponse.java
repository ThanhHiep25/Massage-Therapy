package com.example.spa.dto.response;

import com.example.spa.entities.ServiceSpa;
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
    private String imageUrl;
    private String serviceType;
    private List<ServiceStepDTO> steps; // Thay vì trả về ServiceStep, dùng DTO

    public ServiceSpaResponse(ServiceSpa serviceSpa, List<ServiceStep> steps) {
        this.id = serviceSpa.getService_id();
        this.name = serviceSpa.getName();
        this.description = serviceSpa.getDescription();
        this.price = serviceSpa.getPrice();
        this.duration = serviceSpa.getDuration();

        // Lấy categoryId từ category của ServiceSpa
        this.categoryId = serviceSpa.getCategories() != null ? serviceSpa.getCategories().getCategoryId() : null;

        // Nếu bạn cần mã hóa Base64 cho imageUrl, bạn có thể làm điều này
        this.imageUrl = serviceSpa.getImageUrl(); // Nếu cần Base64, có thể thêm mã hóa
        this.serviceType = serviceSpa.getService_type();

        // Dùng DTO cho các bước thay vì trả về ServiceStep trực tiếp
        this.steps = steps.stream()
                .map(ServiceStepDTO::new) // Áp dụng DTO cho mỗi bước
                .collect(Collectors.toList());
    }
}
