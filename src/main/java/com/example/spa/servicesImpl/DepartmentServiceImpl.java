package com.example.spa.servicesImpl;

import com.example.spa.dto.request.DepartmentRequest;
import com.example.spa.dto.response.DepartmentResponse;
import com.example.spa.entities.Department;
import com.example.spa.enums.StatusBasic;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.DepartmentRepository;
import com.example.spa.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;


    // Tạo department
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest department) {
        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new AppException(ErrorCode.DEPARTMENT_ALREADY_EXISTED);
        }

        Department newDepartment = new Department();
        newDepartment.setDepartmentName(department.getDepartmentName());
        newDepartment.setDescription(department.getDescription() != null ? department.getDescription() : "Không có");
        newDepartment.setCreatedAt(LocalDateTime.now());
        newDepartment.setUpdatedAt(LocalDateTime.now());
        newDepartment.setStatus(StatusBasic.ACTIVATE);
        Department savedDepartment = departmentRepository.save(newDepartment);
        return DepartmentResponse.builder()
                .departmentId(savedDepartment.getDepartmentId())
                .departmentName(savedDepartment.getDepartmentName())
                .description(savedDepartment.getDescription())
                .createdAt(savedDepartment.getCreatedAt())
                .updatedAt(savedDepartment.getUpdatedAt())
                .status(savedDepartment.getStatus().toString())
                .build();
    }

    // Lay department theo id
    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        return DepartmentResponse.builder()
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .status(department.getStatus().toString())
                .build();
    }

    // Cap nhat department
    @Override
    public DepartmentResponse updateDepartment(Long id, Department department) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        if (departmentRepository.existsByDepartmentNameAndDepartmentIdNot(department.getDepartmentName(), id)) {
            throw new AppException(ErrorCode.DEPARTMENT_ALREADY_EXISTED);
        }

        existingDepartment.setDepartmentName(department.getDepartmentName());
        existingDepartment.setDescription(department.getDescription() != null ? department.getDescription() : existingDepartment.getDescription());
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return DepartmentResponse.builder()
                .departmentId(updatedDepartment.getDepartmentId())
                .departmentName(updatedDepartment.getDepartmentName())
                .description(updatedDepartment.getDescription())
                .createdAt(updatedDepartment.getCreatedAt())
                .updatedAt(updatedDepartment.getUpdatedAt())
                .status(updatedDepartment.getStatus().toString())
                .build();
    }

    // Xoa department
    @Override
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        // You might want to add checks here to see if the department is associated with any staff
        departmentRepository.deleteById(id);
    }

    // Lay tat ca department
    @Override
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> DepartmentResponse.builder()
                        .departmentId(department.getDepartmentId())
                        .departmentName(department.getDepartmentName())
                        .description(department.getDescription())
                        .createdAt(department.getCreatedAt())
                        .updatedAt(department.getUpdatedAt())
                        .status(department.getStatus().toString())
                        .build()
                )
                .collect(Collectors.toList());
    }

    // Kich hoat department
    @Override
    public void activateDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        department.setStatus(StatusBasic.ACTIVATE);
        departmentRepository.save(department);
    }

    // Khoa department
    @Override
    public void deactivateDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
        department.setStatus(StatusBasic.DEACTIVATED);
        departmentRepository.save(department);

    }
}