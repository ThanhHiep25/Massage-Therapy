package com.example.spa.controllers;

import com.example.spa.dto.request.StaffServiceRequest;
import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.Staff;
import com.example.spa.entities.StaffServiceSpa;
import com.example.spa.enums.StaffServiceStatus;
import com.example.spa.services.StaffServiceSpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignment-staff")
@RequiredArgsConstructor
public class StaffServiceSpaController {

    private final StaffServiceSpaService staffServiceSpaService;

    //  Phân công nhân viên vào dịch vụ
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

    // 5. Cập nhật trạng thái chưa phân công nhân viên - dịch vụ
    @PutMapping("/unassigned/{id}")
    @Operation(summary = "Cập nhật trạng thái chưa phân công nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> unassigned(@PathVariable Long id) {
        staffServiceSpaService.Unassigned(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }


    // 6. Cập nhật trạng thái đang phân công nhân viên - dịch vụ
    @PutMapping("/assigning/{id}")
    @Operation(summary = "Cập nhật trạng thái đang phân công nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> assigning(@PathVariable Long id) {
        staffServiceSpaService.Assigning(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 7. Cập nhật trạng thái đã phân công nhân viên - dịch vụ
    @PutMapping("/assigned/{id}")
    @Operation(summary = "Cập nhật trạng thái đã phân công nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> assigned(@PathVariable Long id) {
        staffServiceSpaService.Assigned(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 8. Cập nhật trạng thái đang thực hiện nhân viên - dịch vụ
    @PutMapping("/in-progress/{id}")
    @Operation(summary = "Cập nhật trạng thái đang thực hiện nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> inProgress(@PathVariable Long id) {
        staffServiceSpaService.InProgress(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 9. Cập nhật trạng thái đã hoàn thành nhân viên - dịch vụ
    @PutMapping("/completed/{id}")
    @Operation(summary = "Cập nhật trạng thái đã hoàn thành nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> completed(@PathVariable Long id) {
        staffServiceSpaService.Completed(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 10. Cập nhật trạng thái đã hủy nhân viên - dịch vụ
    @PutMapping("/cancelled/{id}")
    @Operation(summary = "Cập nhật trạng thái đã hủy nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> cancelled(@PathVariable Long id) {
        staffServiceSpaService.cancelled(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 11. Cập nhật trạng thái chờ phê duyệt nhân viên - dịch vụ
    @PutMapping("/approval/{id}")
    @Operation(summary = "Cập nhật trạng thái chờ phê duyệt nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> approval(@PathVariable Long id) {
        staffServiceSpaService.Approval(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 12. Cập nhật trạng thái đã quá hạn nhân viên - dịch vụ
    @PutMapping("/overdue/{id}")
    @Operation(summary = "Cập nhật trạng thái đã quá hạn nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<String> overdue(@PathVariable Long id) {
        staffServiceSpaService.Overdue(id);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    // 13. Thống kê số lượng phân công nhân viên - dịch vụ
    @GetMapping("/count-all")
    @Operation(summary = "Thống kê số lượng phân công nhân viên - dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<Long> countAllStaffServices() {
        return ResponseEntity.ok(staffServiceSpaService.countAllStaffServices());
    }

    // 14. Thống kê số lượng phân công theo trạng thái
    @GetMapping("/count-by-status/{status}")
    @Operation(summary = "Thống kê số lượng phân công theo trạng thái")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<Long> countStaffServicesByStatus(@PathVariable StaffServiceStatus status) {
        return ResponseEntity.ok(staffServiceSpaService.countStaffServicesByStatus(status));
    }

    // 15. Thống kê số lượng dịch vụ được giao cho một nhân viên
    @GetMapping("/count-services-by-staff/{staffId}")
    @Operation(summary = "Thống kê số lượng dịch vụ được giao cho một nhân viên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<Long> countServiceByStaffId(@PathVariable Long staffId) {
        return ResponseEntity.ok(staffServiceSpaService.countServiceByStaffId(staffId));
    }

    // 16. Thống kê số lượng nhân viên giao dịch vụ
    @GetMapping("/count-staff-by-service/{serviceId}")

    public ResponseEntity<Long> countStaffByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(staffServiceSpaService.countStaffByServiceId(serviceId));
    }

    // 17. Thống kê số lượng dịch vụ được giao cho mỗi nhân viên
    @GetMapping("/count-services-assigned-to-all-staff")
    public ResponseEntity<Map<Staff, Long>> countServicesAssignedToAllStaff() {
        return ResponseEntity.ok(staffServiceSpaService.countServicesAssignedToAllStaff());
    }

}
