package com.example.spa.controllers;


import com.example.spa.dto.request.PaymentOrderRequest;
import com.example.spa.dto.response.OrderResponse;
import com.example.spa.entities.PaymentOrder;
import com.example.spa.services.OrderService;
import com.example.spa.services.PaymentOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment-order")
@RequiredArgsConstructor
public class PaymentOrderController {
    @Value("${vnp_PaySuccessUrl}")
    private String paymentSuccessUrl ;

    private final PaymentOrderService paymentOrderService;

    private final OrderService orderService;

    // Tạo URL thanh toán VNPAY
    @PostMapping("/vnpay")
    @Operation(summary = "Tạo thanh toán VNPAY", description = "Tạo URL thanh toán VNPAY",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tạo thành công, trả về URL thanh toán"),
                    @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch hẹn")
            }
    )
    public ResponseEntity<?> createVNPayPayment(@Valid @RequestBody PaymentOrderRequest payload) {
        try {
            Long orderId = payload.getOrderId();
            Long amount = payload.getAmount();

            // Lấy thông tin lịch hẹn (AppointmentResponse) từ service
            OrderResponse orderResponse = orderService.getOrderById(orderId);

            if (orderResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new PaymentVNPayController.ErrorResponse("Không tìm thấy lịch hẹn với ID: " + orderId));
            }

            // Gọi service để tạo URL thanh toán VNPay, truyền vào AppointmentResponse
            String paymentUrl = paymentOrderService.createVNPayPayment(orderResponse, amount);

            if (paymentUrl != null && paymentUrl.startsWith("http")) {
                return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentVNPayController.ErrorResponse("Lỗi tạo URL thanh toán VNPay."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentVNPayController.ErrorResponse("Lỗi trong quá trình tạo thanh toán: " + e.getMessage()));
        }
    }

    // Xử lý callback từ VNPAY
    @GetMapping("/vnpay/callback")
    @Operation(summary = "Xử lý callback từ VNPAY", description = "Xử lý kết quả trả về từ cổng thanh toán VNPAY",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Redirect về trang thành công hoặc thất bại"),
                    @ApiResponse(responseCode = "500", description = "Lỗi máy chủ khi xử lý callback")
            }
    )
    public ResponseEntity<?> handleVNPayCallback(@RequestParam Map<String, String> params) {
        try {
            String responseCode = params.get("vnp_ResponseCode");
            String transactionStatus = params.get("vnp_TransactionStatus");
            String transactionNo = params.get("vnp_TransactionNo");
            String amount = params.get("vnp_Amount");
            String bankCode = params.get("vnp_BankCode");
            String orderInfor = params.get("vnp_OrderInfo");
            LocalDateTime transactionTime = params.get("vnp_PayDate") != null && !params.get("vnp_PayDate").isEmpty()
                    ? LocalDateTime.parse(params.get("vnp_PayDate"), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    : null;

            if ("00".equalsIgnoreCase(responseCode)) {
                paymentOrderService.handleVPNPaymentCallback(params);
                StringBuilder redirectUrlBuilder = new StringBuilder();
                redirectUrlBuilder.append(paymentSuccessUrl)
                        .append("vnp_TransactionStatus=").append(transactionStatus)
                        .append("&vnp_TransactionNo=").append(transactionNo)
                        .append("&vnp_Amount=").append(amount)
                        .append("&vnp_BankCode=").append(bankCode)
                        .append("&vnp_OrderInfo=").append(orderInfor);
                if (transactionTime != null) {
                    redirectUrlBuilder.append("&vnp_PayDate=").append(transactionTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrlBuilder.toString()).build();

            } else {
                StringBuilder redirectUrlBuilder = new StringBuilder();
                redirectUrlBuilder.append(paymentSuccessUrl) // Chuyển hướng đến trang thất bại
                        .append("vnp_TransactionStatus=").append(transactionStatus)
                        .append("&vnp_TransactionNo=").append(transactionNo)
                        .append("&vnp_OrderInfo=").append(orderInfor);
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrlBuilder.toString()).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentVNPayController.ErrorResponse("Lỗi xử lý callback từ VNPay: " + e.getMessage()));
        }
    }

    @GetMapping("/vnpay/transaction/{id}")
    @Operation(summary = "Lấy thông tin giao dịch theo ID", description = "Trả về thông tin giao dịch thanh toán theo ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tìm thấy giao dịch thành công"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch với ID được cung cấp")
            }
    )
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Optional<PaymentOrder> optional = paymentOrderService.findById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PaymentVNPayController.ErrorResponse("Không tìm thấy giao dịch với ID: " + id));
        }
    }

    // Lấy tất cả thanh toán
    @GetMapping("/vnpay/transactions")
    @Operation(summary = "Lấy tất cả thanh toán", description = "Trả về tất cả giao dịch thanh toán",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
            }
    )
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(paymentOrderService.findAll());
    }

    // Thống kê tổng thanh toán
    @GetMapping("/vnpayOrder/total")
    @Operation(summary = "Thống kê tổng thanh toán", description = "Trả về tất cả giao dịch thanh toán",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
            }
    )
    public ResponseEntity<?> getTotalPayment() {
        return ResponseEntity.ok(paymentOrderService.countPayments());
    }

    // Thống kê tổng amount payment
    @GetMapping("/amountOrder/total")
    @Operation(summary = "Thống kê tổng thanh toán", description = "Trả về tất cả giao dịch thanh toán",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
            }
    )
    public ResponseEntity<?> getTotalPayments() {
        DecimalFormat defaultFormat = new DecimalFormat("#,##0.00");
        BigDecimal totalAmount = paymentOrderService.getSumAmount();
        return new ResponseEntity<>(defaultFormat.format(totalAmount), HttpStatus.OK);
    }

    // Thống kê thổng tiền thanh toán theo tháng
    @GetMapping("/revenueOrder/monthly")
    @Operation(summary = "Thống kê thổng tiền thanh toán theo tháng", description = "Trả về thống kê thổng tiền thanh toán theo tháng",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hợp lệ"),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch")
            }
    )
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue() {
        List<Map<String, Object>> monthlyRevenue = paymentOrderService.getMonthlyPaymentRevenue();
        return new ResponseEntity<>(monthlyRevenue, HttpStatus.OK);
    }

}
