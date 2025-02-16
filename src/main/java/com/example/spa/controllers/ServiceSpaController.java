package com.example.spa.controllers;

import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.servicesImpl.CloudinaryService;
import com.example.spa.services.ServiceSpaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-spa")
public class ServiceSpaController {

    @Autowired
    private ServiceSpaService serviceSpaService;

    @PostMapping
    @Operation(summary = "createServiceSpa", description = "Tạo dịch vụ spa"
            , responses = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    }
    )
    public ResponseEntity<?> createServiceSpa(@RequestBody ServiceSpaRequest request, HttpServletRequest requestHttp) {
        ServiceSpa serviceSpa = serviceSpaService.createServiceSpa(request.toServiceSpa(), request.getSteps());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ServiceSpaResponse(serviceSpa, request.getSteps()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin dịch vụ spa theo id", description = "Trả về thông tin dịch vụ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "400", description = "Không hợp lệ")
            }
    )
    public ResponseEntity<ServiceSpaResponse> getServiceSpaById(@PathVariable Long id) {
        ServiceSpa serviceSpa = serviceSpaService.getServiceSpaById(id);
        return ResponseEntity.ok(new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps()));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả dịch vụ", description = "Trả về thông tin dịch vụ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "400", description = "Không hợp lệ")
            })
    public ResponseEntity<List<ServiceSpaResponse>> getAllServiceSpas() {
        List<ServiceSpaResponse> response = serviceSpaService.getAllServiceSpas()
                .stream()
                .map(serviceSpa -> new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Chỉnh sửa thông tin dịch vụ", description = "Trả về thông tin dịch vụ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "400", description = "Không hợp lệ")
            })
    public ResponseEntity<ServiceSpaResponse> updateServiceSpa(@PathVariable Long id,
                                                               @RequestBody ServiceSpaRequest request) {
        ServiceSpa updatedService = serviceSpaService.updateServiceSpa(id, request.toServiceSpa(), request.getSteps());
        return ResponseEntity.ok(new ServiceSpaResponse(updatedService, request.getSteps()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông tin dịch vụ spa theo id", description = "Trả về thông báo", responses = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    }
    )
    public ResponseEntity<String> deleteServiceSpa(@PathVariable Long id) {
        serviceSpaService.deleteServiceSpa(id);
        return ResponseEntity.ok("ServiceSpa deleted successfully");
    }

    @PostMapping("/import")
    @Operation(summary = "Import danh sách dịch vụ từ JSON", description = "Lưu danh sách dịch vụ vào database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "400", description = "Không hợp lệ")
            }
    )
    public ResponseEntity<?> importServiceSpas(@RequestBody List<ServiceSpaRequest> requests) {
        List<String> errors = serviceSpaService.importServiceSpas(requests);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok("Import thành công " + requests.size() + " dịch vụ!");
    }

    @PostMapping("/import-file")
    @Operation(summary = "Import dịch vụ từ file JSON", description = "Nhận file JSON và lưu vào database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "400", description = "Không hợp lệ")
            }
    )
    public ResponseEntity<?> importFromFile(@RequestParam("file") MultipartFile file) {
        List<String> errors = serviceSpaService.importServiceSpasFromFile(file);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok("Import thành công!");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error uploading image to Cloudinary: " + e.getMessage());
    }
}
