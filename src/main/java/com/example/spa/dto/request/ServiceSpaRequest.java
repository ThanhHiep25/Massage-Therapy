package com.example.spa.dto.request;

import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import lombok.*;

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
    private String imageUrl; // Nhận image từ FE, không lấy từ Cloudinary

    public ServiceSpa toServiceSpa() {
        ServiceSpa serviceSpa = new ServiceSpa();
        serviceSpa.setName(this.name);
        serviceSpa.setDescription(this.description);
        serviceSpa.setPrice(this.price);
        serviceSpa.setDuration(this.duration);
        serviceSpa.setService_type(this.serviceType);
        serviceSpa.setImageUrl(this.imageUrl);  // Lấy ảnh từ request FE
        return serviceSpa;
    }
}
