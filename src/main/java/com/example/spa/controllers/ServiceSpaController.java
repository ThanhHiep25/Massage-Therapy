package com.example.spa.controllers;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.services.ServiceSpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-spa")
public class ServiceSpaController {

    @Autowired
    private ServiceSpaService serviceSpaService;

    @PostMapping
    @Operation(summary = "Tạo dịch vụ spa", description = "Thêm mới một dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> createServiceSpa(@RequestBody ServiceSpaRequest serviceSpaRequest) {
      try {
          List<ServiceStep> steps = serviceSpaRequest.getSteps(); // Lấy danh sách bước từ request
          ServiceSpa createdService = serviceSpaService.createServiceSpa(serviceSpaRequest, steps);
          return ResponseEntity.ok(createdService);
      }catch (AppException e){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                  "errorCode", "SERVICE_CREATION_FAILED",
                  "message", "Có lỗi xảy ra khi tạo dịch vụ"
          ));
      }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin dịch vụ spa theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<ServiceSpaResponse> getServiceSpaById(@PathVariable Long id) {
        ServiceSpa serviceSpa = serviceSpaService.getServiceSpaById(id);
        return ResponseEntity.ok(new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps()));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<ServiceSpaResponse>> getAllServiceSpas() {
        List<ServiceSpaResponse> response = serviceSpaService.getAllServiceSpas()
                .stream()
                .map(serviceSpa -> new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> updateServiceSpa(@PathVariable Long id,
                                                               @RequestBody ServiceSpaRequest request) {

        try {
            List<ServiceStep> steps = request.getSteps(); // Lấy danh sách bước từ request
            ServiceSpa updatedService = serviceSpaService.updateServiceSpa(id, request, steps);
            return ResponseEntity.ok(new AppException(ErrorCode.SERVICE_UPDATED));
        }catch (AppException e){
            return  ResponseEntity.status(400).body(new AppException(ErrorCode.SERVICE_ERROR));
        }

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa dịch vụ spa theo ID")
    public ResponseEntity<String> deleteServiceSpa(@PathVariable Long id) {
        serviceSpaService.deleteServiceSpa(id);
        return ResponseEntity.ok("ServiceSpa deleted successfully");
    }

    @PostMapping("/import")
    @Operation(summary = "Import danh sách dịch vụ từ JSON", description = "Lưu danh sách dịch vụ vào database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi yêu cầu không hợp lệ")
    })
    public ResponseEntity<?> importServiceSpas(@RequestBody List<ServiceSpaRequest> requests) {
        Map<String, Object> result = serviceSpaService.importServiceSpas(requests);

        if ("error".equals(result.get("status"))) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/import-file")
    @Operation(summary = "Import dịch vụ từ file JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi yêu cầu không hợp lệ")
    })
    public ResponseEntity<?> importFromFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = serviceSpaService.importServiceSpasFromFile(file);

        if ("error".equals(result.get("status"))) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    @Operation(summary = "Xóa tất cả dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<String> deleteAllServiceSpas() {
        serviceSpaService.deleteAllServiceSpas();
        return ResponseEntity.ok("All ServiceSpas deleted successfully");
    }

    @GetMapping("/search/{serviceName}")
    @Operation(summary = "Tìm dịch vụ spa theo tên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<ServiceSpa> findByServiceSpaName(@PathVariable String serviceName) {
        ServiceSpa serviceSpa = serviceSpaService.findByServiceName(serviceName);
        return ResponseEntity.ok(serviceSpa);
    }

    @GetMapping("/exists/{serviceName}")
    @Operation(summary = "Kiểm tra dịch vụ spa đã tồn tại hay chưa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Boolean> existsByServiceName(@PathVariable String serviceName) {
        return ResponseEntity.ok(serviceSpaService.existsByServiceName(serviceName));
    }

    @PutMapping("/deactivate/{id}")
    @Operation(summary = "Deactivate dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<String> deactivateServiceSpa(@PathVariable Long id) {
        serviceSpaService.deactivateServiceSpa(id);
        return ResponseEntity.ok("ServiceSpa deactivated successfully");
    }

    @PutMapping("/activate/{id}")
    @Operation(summary = "Activate dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<String> activateServiceSpa(@PathVariable Long id) {
        serviceSpaService.activateServiceSpa(id);
        return ResponseEntity.ok("ServiceSpa activated successfully");
    }
}
