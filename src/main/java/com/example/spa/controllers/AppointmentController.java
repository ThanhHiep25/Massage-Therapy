package com.example.spa.controllers;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.DashboardStatsResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.enums.AppointmentStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.services.AppointmentService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;


    // Tạo lịch hẹn
    @PostMapping("/create")
    @Operation(summary = "Tạo lịch hẹn", description = "Tạo lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Không hợp lệ")
    })
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        AppointmentResponse appointment = appointmentService.createAppointment(request);
        return ResponseEntity.ok(appointment);

    }

    // Lấy lịch hẹn theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin lịch hẹn theo id", description = "Trả về thông tin lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    // Lấy tất cả lịch hẹn
    @GetMapping("/")
    @Operation(summary = "Lấy tất cả lịch hẹn", description = "Trả về tất cả lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    // Lấy lịch hẹn theo user id
    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy lịch hẹn theo user id", description = "Trả về lịch hẹn theo user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUserId(userId));
    }

    // Cập nhật lịch hẹn
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật lịch hẹn", description = "Cập nhật lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        Appointment updatedAppointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(updatedAppointment);
    }


    // Xóa lịch hẹn
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa lịch hẹn", description = "Xóa lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("Xóa lịch hẹn thành công");

    }


    // Lọc lịch hẹn theo trạng thái
    @GetMapping("/status/{status}")
    @Operation(summary = "Lọc lịch hẹn theo trạng thái", description = "Trả về tất cả lịch hẹn theo trạng thái")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByStatus(status));
    }

    // Lọc lịch hẹn theo ngày
    @GetMapping("/date/{date}")
    @Operation(summary = "Lọc lịch hẹn theo ngày", description = "Trả về tất cả lịch hẹn theo ngày")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(@PathVariable String date) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByDate(date));
    }

    // Trạng thái người ùng dang thực hiện việc đặt lich
    @PutMapping("/{id}/in-progress")
    @Operation(summary = "Trạng thái người ùng dang thực hiện việc đặt lich", description = "Trạng thái người ùng dang thực hiện việc đặt lich")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> inProgressAppointment(@PathVariable Long id) {
        appointmentService.inProgress(id);
        return ResponseEntity.ok("Trạng thái người ùng dang thực hiện việc đặt lich");
    }

    // Chuyển trạng thái lịch hẹn sang chờ xác nhận
    @PutMapping("/{id}/pending")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang chờ xác nhận", description = "Chuyển trạng thái lịch hen sang cho xác nhận")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> pendingAppointment(@PathVariable Long id) {
        appointmentService.pendingAppointment(id);
        return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang chờ xác nhận");
    }

    // Chuyển trạng thái lịch hẹn sang đã hủy
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang đã hủy", description = "Chuyển trạng thái lịch hẹn sang đã hủy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang đã hủy");
    }

    // Chuyển trạng thái lịch hẹn sang đã hoàn thành
    @PutMapping("/{id}/complete")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang đã hoàn thành", description = "Chuyển trạng thái lịch hẹn sang đã hoàn thành")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        appointmentService.completeAppointment(id);
        return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang đã hoàn thành");
    }

    // Chuyển trạng thái lịch hẹn sang đã đặt lịch
    @PutMapping("/{id}/scheduled")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang đã đặt lịch", description = "Chuyển trạng thái lịch hẹn sang đã đặt lịch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> scheduledAppointment(@PathVariable Long id) {
        appointmentService.scheduledAppointment(id);
        return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang đã đặt lịch");

    }

    @PutMapping("/{id}/paid")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang thanh toán", description = "Chuyển trạng thái lịch hẹn sang thanh toán")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<?> paidAppointment(@PathVariable Long id) {
        appointmentService.paidAppointment(id);
        return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang thanh toán");
    }

    // Thống kê tổng số lịch hẹn theo trạng thái
    @GetMapping("/count-by-status")
    @Operation(summary = "Thống kê tổng số lịch hẹn theo trạng thái", description = "Trả về tất cả lịch hẹn theo trạng thái")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public Map<AppointmentStatus, Long> countByStatus() {
        return appointmentService.countAppointmentsByStatus();
    }

    // Đếm lịch hen hôm nay
    @GetMapping("/count-today")
    @Operation(summary = "Đếm lịch hẹn hóm nay", description = "Trả về số lịch hẹn hóm nay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public long countToday() {
        return appointmentService.countAppointmentsToday();
    }


    // Thống kê theo tháng/ năm - tổng số lịch hẹn
    @GetMapping("/count-by-month/{year}/{month}")
    @Operation(summary = "Thống kê theo tháng/ năm tổng số lịch hẹn", description = "Trả về tất cả lịch hẹn theo tháng/ năm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    }
    )
    public long countByMonth(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return appointmentService.countAppointmentsByMonth(year, month);
    }

    // Thống kê tổng lịch hẹn
    @GetMapping("/count-all")
    @Operation(summary = "Thống kê tổng lịch hẹn", description = "Trả về tất cả lịch hẹn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    }
    )
   public ResponseEntity<?> countAll() {
        return ResponseEntity.ok(appointmentService.countAppointments());
    }


    @GetMapping("/export/excel")
    @Operation(summary = "Xuat danh sach lich hen thanh file excel", description = "Xuat danh sach lich hen thanh file excel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public ResponseEntity<byte[]> exportAppointmentsToExcel() throws IOException {
        List<AppointmentResponse> appointments = appointmentService.getAllAppointments(); // Hoặc một phương thức khác để lấy danh sách lịch hẹn cần xuất
        byte[] excelBytes = appointmentService.exportAppointmentsToExcel(appointments);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = "DanhSachLichHen_" + formatter.format(now) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

}
