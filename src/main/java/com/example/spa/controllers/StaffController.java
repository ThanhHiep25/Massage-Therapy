package com.example.spa.controllers;

import com.example.spa.dto.request.StaffRequest;
import com.example.spa.dto.response.StaffResponse;
import com.example.spa.entities.Staff;
import com.example.spa.exception.AppException;
import com.example.spa.services.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @Operation(summary = "Lấy tất cả nhân viên", description = "In ra tất cả nhân viên",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
            }
    )
    public ResponseEntity<List<StaffResponse>> getAllStaffs() {
        return ResponseEntity.ok(staffService.getAllStaffs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy nhân viên theo ID", description = "In ra nhân viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
            }
    )
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @PostMapping
    @Operation(summary = "Thêm nhân viên", description = "Thêm mới nhân viên",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tạo thành công"),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
            }
    )
    public ResponseEntity<Staff> createStaff(@Valid @RequestBody StaffRequest staffRequest) {
        return ResponseEntity.ok(staffService.createStaff(staffRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nhân viên", description = "Cập nhật nhân viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
            }
    )
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody StaffRequest staffRequest) {
        return ResponseEntity.ok(staffService.updateStaff(id, staffRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhân viên", description = "Xóa nhân viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
            }
    )
    public ResponseEntity<String> deleteStaff(@PathVariable Long id) {
            staffService.deleteStaff(id);
            return ResponseEntity.ok("Nhân viên đã được xóa.");
    }

    @PostMapping("/import-json")
    @Operation(summary = "Import nhân viên từ JSON", description = "Import nhân viên từ JSON",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Import thành công"),
                    @ApiResponse(responseCode = "400", description = "Dữ liệu JSON không hợp lệ")
            }
    )
    public ResponseEntity<List<Staff>> importStaffsFromJson(@RequestBody String json) {
        try {
            return ResponseEntity.ok(staffService.importStaffsFromJson(json));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/import-file")
    @Operation(summary = "Import nhân viên từ tệp", description = "Import nhân viên từ tệp JSON",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Import thành công"),
                    @ApiResponse(responseCode = "400", description = "File không hợp lệ")
            }
    )
    public ResponseEntity<?> importStaffsFromFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(staffService.importStaffsFromFile(file));
        } catch (AppException e) {
            return ResponseEntity.badRequest().body("Lỗi khi đọc file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi máy chủ.");
        }
    }



    @PutMapping("/{id}/activate")
    @Operation(summary = "Kích hoạt nhân viên", description = "Kích hoạt nhân viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kích hoạt thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
            }
    )
    public ResponseEntity<String> activateStaff(@PathVariable Long id) {
        staffService.activateStaff(id);
        return ResponseEntity.ok("Nhân viên đã được kích hoạt.");
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Vô hiệu nhân viên", description = "Vô hiệu nhân viên theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vô hiệu thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")

            }
    )
    public ResponseEntity<String> deactivateStaff(@PathVariable Long id) {
        staffService.deactivateStaff(id);
        return ResponseEntity.ok("Nhân viên đã được vô hiệu.");
    }

    // Thống kê tổng nhân viên
    @GetMapping("/count")
    public ResponseEntity<?> countStaffs() {
        return ResponseEntity.ok(staffService.countStaffs());
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportStaffsToExcel() throws IOException {
        List<StaffResponse> staffs = staffService.getAllStaffs(); // Hoặc phương thức khác để lấy danh sách nhân viên
        byte[] excelBytes = staffService.exportStaffsToExcel(staffs);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "DanhSachNhanVien_" + formatter.format(now) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
