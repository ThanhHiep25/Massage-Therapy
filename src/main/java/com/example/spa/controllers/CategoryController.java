package com.example.spa.controllers;

import com.example.spa.dto.request.CategoryRequest;
import com.example.spa.entities.Categories;
import com.example.spa.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Tạo danh mục mới
    @PostMapping
    @Operation(summary = "Tạo danh mục mới", description = "Thêm danh mục mới vào hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<Categories> createCategory(@RequestBody CategoryRequest request) {
        Categories category = categoryService.createCategory(request);
        return ResponseEntity.ok(category);
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy danh mục theo ID", description = "Trả về danh mục theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<Categories> getCategoryById(@PathVariable Long id) {
        Categories category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // Lấy tất cả danh mục
    @GetMapping
    @Operation(summary = "Lấy tất cả danh mục", description = "Trả về danh sách tất cả danh mục")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = (List<Categories>) categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Cập nhật danh mục
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật danh mục", description = "Chỉnh sửa thông tin danh mục")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<Categories> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Categories updatedCategory = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    // Xóa danh mục theo ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa danh mục", description = "Xóa danh mục theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Xóa tất cả danh mục
    @DeleteMapping("/delete-all")
    @Operation(summary = "Xóa tất cả danh mục", description = "Xóa toàn bộ danh mục trong hệ thống")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa thành công")
    })
    public ResponseEntity<Void> deleteAllCategories() {
        categoryService.deleteAllCategories();
        return ResponseEntity.noContent().build();
    }

    // Kiểm tra danh mục có tồn tại không
    @GetMapping("/exists/{name}")
    @Operation(summary = "Kiểm tra danh mục có tồn tại không", description = "Trả về true nếu danh mục tồn tại")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public ResponseEntity<Boolean> existsByCategoryName(@PathVariable String name) {
        boolean exists = categoryService.existsByCategoryName(name);
        return ResponseEntity.ok(exists);
    }

    // Tìm danh mục theo tên
    @GetMapping("/find/{name}")
    @Operation(summary = "Tìm danh mục theo tên", description = "Trả về danh mục theo tên")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<Categories> findByCategoryName(@PathVariable String name) {
        Categories category = categoryService.findByCategoryName(name);
        return ResponseEntity.ok(category);
    }

    // Thống kê tổng danh mục
    @GetMapping("/count")
    @Operation(summary = "Thống kê tổng danh mục", description = "Trả về số lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy danh mục")
    })
    public ResponseEntity<Long> countCategories() {
        long count = categoryService.countCategories();
        return ResponseEntity.ok(count);
    }
}
