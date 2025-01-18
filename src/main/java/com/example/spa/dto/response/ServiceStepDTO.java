package com.example.spa.dto.response;

import com.example.spa.entities.ServiceStep;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceStepDTO {
    private Long stepId;
    private Integer stepOrder;
    private String description;

    // Constructor từ entity ServiceStep
    public ServiceStepDTO(ServiceStep serviceStep) {
        this.stepId = serviceStep.getStep_id();
        this.stepOrder = serviceStep.getStepOrder();
        this.description = serviceStep.getDescription();
    }
}
