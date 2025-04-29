package com.example.spa.dto.response;

import com.example.spa.entities.Department;
import com.example.spa.enums.StatusBasic;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {

    private Long departmentId;

    private String departmentName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    public DepartmentResponse(Department department) {
        this.departmentId = department.getDepartmentId();
        this.departmentName = department.getDepartmentName();
        this.description = department.getDescription();
        this.createdAt = department.getCreatedAt();
        this.updatedAt = department.getUpdatedAt();
        this.status = department.getStatus().toString();
    }
}
