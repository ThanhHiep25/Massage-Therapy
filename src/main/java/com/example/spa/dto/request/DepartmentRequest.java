package com.example.spa.dto.request;


import com.example.spa.enums.StatusBasic;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequest {

    private String departmentName;

    private String description;

}
