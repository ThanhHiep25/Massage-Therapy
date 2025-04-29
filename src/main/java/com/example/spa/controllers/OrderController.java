package com.example.spa.controllers;

import com.example.spa.dto.request.CreateOrderRequest;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Tạo đơn hàng mới", description = "Thêm đơn hàng mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        OrderResponse createdOrder = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lay thong tin don hang theo id", description = "Lay thong tin don hang theo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lay thong tin don hang thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Lay thong tin don hang theo id nguoi dung", description = "Lay thong tin don hang theo id nguoi dung")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lay thong tin don hang thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    @Operation(summary = "Lay thong tin don hang", description = "Lay thong tin don hang")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lay thong tin don hang thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cap nhat trang thai don hang", description = "Cap nhat trang thai don hang")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoa don hang", description = "Xoa don hang")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xoa thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}