package com.example.spa.repositories;

import com.example.spa.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByDepartmentName(String departmentName);

    List<Department> findByStatus(String status);

    boolean existsByDepartmentNameAndDepartmentIdNot(String departmentName, Long id);

}
