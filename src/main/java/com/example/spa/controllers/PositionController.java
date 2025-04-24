package com.example.spa.controllers;

import com.example.spa.entities.Position;
import com.example.spa.services.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    @Operation(summary = "Lấy tất cả chức vụ", description = "Trả về danh sách tất cả chức vụ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
                    @ApiResponse(responseCode = "400", description = "Lỗi request không hợp lệ")
            }
    )
    public ResponseEntity<List<Position>> getAllPositions() {
        return ResponseEntity.ok(positionService.getAllPosition());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chức vụ theo ID", description = "Trả về thông tin chức vụ theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lấy chức vụ thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chức vụ")
            }
    )
    public ResponseEntity<Position> getPositionById(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.getPositionById(id));
    }

    @PostMapping
    @Operation(summary = "Thêm chức vụ mới", description = "Thêm chức vụ vào hệ thống",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Thêm thành công"),
                    @ApiResponse(responseCode = "400", description = "Lỗi request không hợp lệ")
            }
    )
    public ResponseEntity<Position> createPosition(@RequestBody Position position) {
        return ResponseEntity.ok(positionService.createPosition(position));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật chức vụ", description = "Cập nhật thông tin chức vụ theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chức vụ")
            }
    )
    public ResponseEntity<Position> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        return ResponseEntity.ok(positionService.updatePosition(id, position));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa chức vụ", description = "Xóa chức vụ theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Xóa thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chức vụ")
            }
    )
    public ResponseEntity<String> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.ok("Chức vụ đã được xóa thành công.");
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm chức vụ theo tên", description = "Trả về thông tin chức vụ dựa trên tên",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tìm thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy chức vụ")
            }
    )
    public ResponseEntity<Position> findByName(@RequestParam String name) {
        Position position = positionService.findByName(name);
        return position != null ? ResponseEntity.ok(position) : ResponseEntity.notFound().build();
    }


    @PostMapping("/import-json")
    @Operation(summary = "Import chức vụ từ JSON", description = "Thêm nhiều chức vụ từ JSON",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Import thành công"),
                    @ApiResponse(responseCode = "400", description = "Lỗi request không hợp lệ")
            }
    )
    public ResponseEntity<List<Position>> importPositionsFromJson(@RequestBody String json) {
        return ResponseEntity.ok(positionService.importPostionFromJson(json));
    }

    @PostMapping("/import-file")
    @Operation(summary = "Import chức vụ từ file JSON", description = "Thêm nhiều chức vụ từ file JSON",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Import thành công"),
                    @ApiResponse(responseCode = "400", description = "Lỗi file hoặc request không hợp lệ")
            }
    )
    public ResponseEntity<List<Position>> importPositionsFromFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(positionService.importPositionsFromFile(file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
