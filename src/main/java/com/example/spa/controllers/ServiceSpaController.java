package com.example.spa.controllers;

import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceStep;
import com.example.spa.servicesImpl.CloudinaryService;
import com.example.spa.services.ServiceSpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-spa")
public class ServiceSpaController {

    @Autowired
    private ServiceSpaService serviceSpaService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    @Operation(summary = "createServiceSpa", description = "Tạo dịch vụ spa")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> createServiceSpa(@RequestBody ServiceSpaRequest request) {
        try {
            String imageUrl = null; // Mặc định là null

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                imageUrl = cloudinaryService.uploadImage(request.getImage());
            } else {
                imageUrl = "https://example.com/default-image.png"; // URL mặc định
            }

            // Chuyển request thành ServiceSpa
            ServiceSpa serviceSpa = request.toServiceSpa(imageUrl);

            // Lưu ServiceSpa và lấy danh sách các steps
            ServiceSpa createdServiceSpa = serviceSpaService.createServiceSpa(serviceSpa, request.getSteps());

            // Tạo ServiceSpaResponse để trả về
            ServiceSpaResponse response = new ServiceSpaResponse(createdServiceSpa, request.getSteps());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image to Cloudinary: " + e.getMessage());
        }
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error uploading image to Cloudinary: " + e.getMessage());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin dịch vụ spa theo id", description = "Trả về thông tin dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> getServiceSpaById(@PathVariable Long id) {
        ServiceSpa serviceSpa = serviceSpaService.getServiceSpaById(id);
        if (serviceSpa == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        ServiceSpaResponse response = new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    @Operation(summary = "Lấy tất cả dịch vụ", description = "Trả về thông tin dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<List<ServiceSpaResponse>> getAllServiceSpas() {
        // Lấy tất cả các ServiceSpa từ serviceSpaService
        List<ServiceSpa> serviceSpas = serviceSpaService.getAllServiceSpas();

        // Chuyển đổi mỗi ServiceSpa thành ServiceSpaResponse
        List<ServiceSpaResponse> response = serviceSpas.stream()
                .map(serviceSpa -> new ServiceSpaResponse(serviceSpa, serviceSpa.getSteps()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Chỉnh sửa thông tin dịch vụ", description = "Trả về thông tin dịch vụ")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<ServiceSpa> updateServiceSpa(
            @PathVariable Long id,
            @RequestBody ServiceSpa serviceSpa,
            @RequestBody List<ServiceStep> steps) {
        return ResponseEntity.ok(serviceSpaService.updateServiceSpa(id, serviceSpa, steps));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông tin dịch vụ spa theo id", description = "Trả về thông báo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hợp lệ"

            ),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResultResponse<?> deleteServiceSpa(@PathVariable Long id) {
        ServiceSpa serviceSpa = serviceSpaService.getServiceSpaById(id);
        if (serviceSpa == null) {
            return ResultResponse.builder().result("Not found id").build();
        }

        // Xoá ServiceSpa khỏi cơ sở dữ liệu
        serviceSpaService.deleteServiceSpa(id);

        return ResultResponse.builder()
                .message("ServiceSpa deleted successfully")
                .build();
    }

}