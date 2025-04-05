package com.example.spa.controllers;

import com.example.spa.dto.request.StaffServiceRequest;
import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.StaffServiceSpa;
import com.example.spa.services.StaffServiceSpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment-staff")
@RequiredArgsConstructor
public class StaffServiceSpaController {

    private final StaffServiceSpaService staffServiceSpaService;


    @PostMapping
    @Operation(summary = "Phân công nhân viên vào dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> assignStaffToService(@RequestBody StaffServiceRequest request) {
        StaffServiceResponse response = staffServiceSpaService.assignStaffToService(request);
        return ResponseEntity.ok(response);
    }

    //  1. Lấy danh sách tất cả các phân công nhân viên - dịch vụ
    @GetMapping
    @Operation(summary = "Lấy danh sách phân công nhân viên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> getAllStaffServices() {
        List<StaffServiceResponse> staffServices = staffServiceSpaService.getAllStaffServices();
        return ResponseEntity.ok(staffServices);
    }

    // 2. Lấy thông tin phân công nhân viên - dịch vụ theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin phân công nhân viên - dịch vụ theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<StaffServiceResponse> getStaffServiceById(@PathVariable Long id) {
        StaffServiceResponse staffService = staffServiceSpaService.getStaffServiceById(id);
        return ResponseEntity.ok(staffService);
    }

    // 3. Cập nhật thông tin phân công nhân viên - dịch vụ
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin phân công nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> updateStaffService(
            @PathVariable Long id,
            @RequestBody StaffServiceRequest request) {
        staffServiceSpaService.updateStaffService(id, request);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 4. Xóa phân công nhân viên - dịch vụ theo ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa phân công nhân viên - dịch vụ theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> deleteStaffService(@PathVariable Long id) {
        staffServiceSpaService.deleteStaffService(id);
        return ResponseEntity.ok("Xóa thành công!");
    }
}
