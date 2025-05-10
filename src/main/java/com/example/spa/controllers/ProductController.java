package com.example.spa.controllers;

import com.example.spa.dto.request.ProductRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.ProductResponse;
import com.example.spa.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @Operation(summary = "Tạo sản phẩm mới", description = "Trả về sản phẩm vừa tạo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    }
    )
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật sản phẩm", description = "Trả về sản phẩm vừa cập nhật")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    }
    )
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin sản phẩm theo id", description = "Trả về thông tin lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Trả về danh sách lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> allProducts = productService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa lịch hẹn", description = "Xóa lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "activate sản phẩm", description = "activate sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/sale")
    @Operation(summary = "Sale sản phẩm", description = "Sale sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Void> saleProduct(@PathVariable Long id) {
        productService.saleProduct(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/sale/{quantity}")
    @Operation(summary = "Kiểm tra lịch hẹn", description = "Kiểm tra lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Void> saleOfProduct(@PathVariable Long id, @PathVariable int quantity) {
        productService.saleOfProduct(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "deactivate sản phẩm", description = "deactivate sản phẩm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok().build();
    }

    // Thống kê số lượng sản phẩm
    @GetMapping("/count")
    @Operation(summary = "Thống kê số lượng sản phẩm", description = "Trả về tất cả lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    }
    )
    public ResponseEntity<?> countProduct() {
        return ResponseEntity.ok(productService.countProduct());
    }

    // Xuat danh sach sản phẩm
    @GetMapping("/export/excel")
    @Operation(summary = "Xuat danh sach san pham thanh file excel", description = "Xuat danh sach san pham thanh file excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<byte[]> exportAppointmentsToExcel() throws IOException {
        List<ProductResponse> products = productService.getAllProducts();
        byte[] excelBytes = productService.exportProductListToExcel(products);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "DanhSachSanPham_" + formatter.format(now) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

}
