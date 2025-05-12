package com.example.spa.controllers;

import com.example.spa.dto.request.DiscountCodeRequest;
import com.example.spa.dto.response.DiscountCodeResponse;
import com.example.spa.entities.DiscountCode;
import com.example.spa.services.DiscountCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount-code")
@RequiredArgsConstructor
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;

  // Tạo khuyến mãi
    @PostMapping("/create")
    @Operation(summary = "Tạo khuyến mãi", description = "Tạo khuyến mãi mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<?> createDiscountCode(@RequestBody DiscountCodeRequest discountCodeRequest) {
        DiscountCode discountCodeResponse = discountCodeService.createDiscountCode(discountCodeRequest);
        return ResponseEntity.ok(discountCodeResponse);
    }

    // Lấy khuyến mãi theo id
    @GetMapping("/{id}")
    @Operation(summary = "Lấy khuyến mãi theo id", description = "Lấy khuyến mãi theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> getDiscountCodeById(@PathVariable Long id) {
        DiscountCodeResponse discountCodeResponse = discountCodeService.getDiscountCodeById(id);
        return ResponseEntity.ok(discountCodeResponse);
    }

    // Lấy tất cả danh sách khuyên mãi
    @GetMapping("/all")
    @Operation(summary = "Lấy tất cả danh sách khuyến mãi", description = "Lấy tất cả danh sách khuyên mái")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy ")
    })
    public ResponseEntity<?> getAllDiscountCode() {
        List<DiscountCodeResponse> discountCodeResponses = discountCodeService.getAllDiscountCode();
        return ResponseEntity.ok(discountCodeResponses);
    }

    // Lấy khuyến mãi theo code
    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy khuyến mãi theo code", description = "Lấy khuyến mãi theo code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> getDiscountCodeByCode(@PathVariable String code) {
        DiscountCode discountCode = discountCodeService.getDiscountCodeByCode(code);
        return ResponseEntity.ok(discountCode);
    }
}
