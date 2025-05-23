package com.example.spa.controllers;

import com.example.spa.dto.request.CreateOrderRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Tạo đơn hàng mới
    @PostMapping("/create")
    @Operation(summary = "Tạo đơn hàng mới", description = "Thêm đơn hàng mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        OrderResponse createdOrder = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // Cập nhật thông tin đơn hàng
    @PutMapping("/{id}")
    @Operation(summary = "Cap nhat thong tin don hang", description = "Cap nhat thong tin don hang")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "400", description = "Du lieu khong hop le")
    })
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @RequestBody CreateOrderRequest updateRequest) {
        OrderResponse updatedOrder = orderService.updateOrder(id, updateRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    // Lấy thống tin đơn hàng theo id
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


    // Lấy thống tin đơn hàng theo id nguoi dung
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

    // Lấy thống tin đơn hàng
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

    // Cap nhat trang thai don hang
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

    // Cập nhật trạng thái Processing
    @PutMapping("/{id}/processing")
    @Operation(summary = "Cap nhat trang thai Processing", description = "Cap nhat trang thai Processing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderProcessing(@PathVariable Long id) {
        orderService.processOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái Processing");
    }

    // Cập nhật trạng thái SHIPPED
    @PutMapping("/{id}/shipped")
    @Operation(summary = "Cap nhat trang thai SHIPPED", description = "Cap nhat trang thai SHIPPED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderShipped(@PathVariable Long id) {
        orderService.shippedOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái SHIPPED");
    }

    // Cập nhật trạng thái DELIVERED
    @PutMapping("/{id}/delivered")
    @Operation(summary = "Cap nhat trang thai DELIVERED", description = "Cap nhat trang thai DELIVERED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderDelivered(@PathVariable Long id) {
        orderService.deliveredOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái DELIVERED");
    }

    // Cập nhật trạng thái CANCELLED
    @PutMapping("/{id}/cancelled")
    @Operation(summary = "Cap nhat trang thai CANCELLED", description = "Cap nhat trang thai CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderCancelled(@PathVariable Long id) {
        orderService.cancelledOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái CANCELLED");
    }

    // Cập nhật trạng thái PAID
    @PutMapping("/{id}/paid")
    @Operation(summary = "Cap nhat trang thai PAID", description = "Cap nhat trang thai PAID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderPaid(@PathVariable Long id) {
        orderService.paidOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái PAID");
    }

    // Cập nhật trạng thái REFUND
    @PutMapping("/{id}/refund")
    @Operation(summary = "Cap nhat trang thai REFUND", description = "Cap nhat trang thai REFUND")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cap nhat thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<?> updateOrderRefund(@PathVariable Long id) {
        orderService.refundOrder(id);
        return ResponseEntity.ok("Chuyển trạng thái REFUND");
    }

    // Xoa don hang
    @DeleteMapping("/{id}")
    @Operation(summary = "Xoa don hang", description = "Xoa don hang")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Xoa thanh cong"),
            @ApiResponse(responseCode = "404", description = "Khong tim thay don hang")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Xuất Order sang Excel
    @GetMapping("/export/excel")
    @Operation(summary = "Xuat danh sach don hang thanh file excel", description = "Xuat danh sach don hang thanh file excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<byte[]> exportOrderToExcel() throws IOException {
        List<OrderResponse> orderResponses = orderService.getAllOrders(); // Hoặc một phương thức khác để lấy danh sách lịch hẹn cần xuất
        byte[] excelBytes = orderService.exportOrdersToExcel(orderResponses);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "DanhSachDonHang_" + formatter.format(now) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}