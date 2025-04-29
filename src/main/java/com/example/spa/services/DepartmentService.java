package com.example.spa.services;

import com.example.spa.dto.request.DepartmentRequest;
import com.example.spa.dto.response.DepartmentResponse;
import com.example.spa.entities.Department;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest department);

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse updateDepartment(Long id, Department department);

    void deleteDepartment(Long id);

    List<DepartmentResponse> getAllDepartments();


    void activateDepartment(Long id);

    void deactivateDepartment(Long id);
}
