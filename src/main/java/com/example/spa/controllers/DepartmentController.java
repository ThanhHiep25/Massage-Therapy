package com.example.spa.controllers;

import com.example.spa.dto.request.DepartmentRequest;
import com.example.spa.dto.response.DepartmentResponse;
import com.example.spa.entities.Department;
import com.example.spa.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/create")
    @Operation(summary = "Tạo phòng mới", description = "Tạo phòng mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Phòng ban đã được tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ")
    })
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponse createdDepartment = departmentService.createDepartment(departmentRequest);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy phòng theo ID", description = "Lấy thông tin phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thông tin phòng ban"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse departmentResponse = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(departmentResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin phòng ban", description = "Cập nhật thông tin phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thông tin phòng ban đã được cập nhật"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody Department department) {
        DepartmentResponse updatedDepartment = departmentService.updateDepartment(id, department);
        return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa phòng ban", description = "Xóa phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Phòng ban đã được xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả phòng ban", description = "Lấy danh sách tất cả các phòng ban")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách tất cả phòng ban")
    })
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> allDepartments = departmentService.getAllDepartments();
        return new ResponseEntity<>(allDepartments, HttpStatus.OK);
    }


    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt phòng ban", description = "Kích hoạt trạng thái của phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phòng ban đã được kích hoạt"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<Void> activateDepartment(@PathVariable Long id) {
        departmentService.activateDepartment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu hóa phòng ban", description = "Vô hiệu hóa trạng thái của phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phòng ban đã được vô hiệu hóa"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<Void> deactivateDepartment(@PathVariable Long id) {
        departmentService.deactivateDepartment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}