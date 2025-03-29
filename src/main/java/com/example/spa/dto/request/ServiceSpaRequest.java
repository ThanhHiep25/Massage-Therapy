package com.example.spa.dto.request;

import com.example.spa.entities.Categories;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.enums.StatusBasic;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    private String serviceType;
    private List<ServiceStep> steps;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;

    public ServiceSpa toServiceSpa(Categories categories) {
        ServiceSpa serviceSpa = new ServiceSpa();
        serviceSpa.setName(this.name);
        serviceSpa.setDescription(this.description);
        serviceSpa.setPrice(this.price);
        serviceSpa.setDuration(this.duration);
        serviceSpa.setCategories(categories);
        serviceSpa.setService_type(this.serviceType);
        serviceSpa.setCreatedAt(LocalDateTime.now());
        serviceSpa.setUpdatedAt(LocalDateTime.now());
        serviceSpa.setStatus(StatusBasic.ACTIVATE);
        return serviceSpa;
    }

}
