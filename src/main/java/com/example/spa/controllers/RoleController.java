package com.example.spa.controllers;

import com.example.spa.dto.request.RoleRequest;
import com.example.spa.entities.Role;
import com.example.spa.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // Thêm role mới
    @PostMapping
    @Operation(summary = "Thêm role mới", description = "Thêm role mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    public ResponseEntity<?> createRole(RoleRequest role) {
        return ResponseEntity.ok(roleService.save(role));
    }


    @GetMapping
    @Operation(summary = "Lấy tất cả role", description = "In ra tất cả role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
