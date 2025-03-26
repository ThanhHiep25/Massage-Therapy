package com.example.spa.services;

import com.example.spa.entities.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department getDepartmentById(Long id);

    Department updateDepartment(Long id, Department department);

    void deleteDepartment(Long id);

    Department getDepartmentByName(String name);

    List<Department> getDepartmentsByName(String name);

    List<Department> getAllDepartments();

    List<Department> getDepartmentsByStatus(String status);

    void activateDepartment(Long id);

    void deactivateDepartment(Long id);
}
