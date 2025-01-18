package com.example.spa.dto.request;

import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceSpaRequest {
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Long categoryId;  // ID của Categories
    private String image;     // Trường image
    private String serviceType;
    private List<ServiceStep> steps;

    public ServiceSpa toServiceSpa(String imageUrl) {
        ServiceSpa serviceSpa = new ServiceSpa();
        serviceSpa.setName(this.name);
        serviceSpa.setDescription(this.description);
        serviceSpa.setPrice(this.price);
        serviceSpa.setDuration(this.duration);
       // serviceSpa.setCategoryId(this.categoryId);
        serviceSpa.setService_type(this.serviceType);
        serviceSpa.setImageUrl(imageUrl);  // Cập nhật hình ảnh
        return serviceSpa;
    }
}

