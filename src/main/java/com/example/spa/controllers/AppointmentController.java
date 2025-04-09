package com.example.spa.controllers;

import com.example.spa.dto.ResultResponse;
import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
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
            return ResponseEntity.ok(appointmentService);
    }

//    // Lọc lịch hẹn theo user
//    @GetMapping("/user/{userId}")
//    @Operation(summary = "Lọc lịch h��n theo user", description = "Trả về tất cả lịch hẹn của user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
//            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
//    })
//    public ResponseEntity<List<Appointment>> getAppointmentsByUserId(@PathVariable Long userId) {
//        return ResponseEntity.ok(appointmentService.getAllAppointmentsByUserId(userId));
//    }
//
//    // Lọc lịch hẹn theo nhân viên
//    @GetMapping("/staff/{staffId}")
//    @Operation(summary = "Lọc lịch hẹn theo nhân viên", description = "Trả về tất cả lịch hẹn của nhân viên")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
//            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
//    })
//    public ResponseEntity<List<Appointment>> getAppointmentsByStaffId(@PathVariable Long staffId) {
//        return ResponseEntity.ok(appointmentService.getAllAppointmentsByStaffId(staffId));
//    }

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

    // Chuyển trạng thái lịch hẹn sang chờ xác nhận
    @PutMapping("/{id}/pending")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang chờ xác nhận", description = "Chuyển trạng thái lịch hen sang cho xác nhận")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public  ResponseEntity<?> pendingAppointment(@PathVariable Long id) {
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
    public  ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(new AppException(ErrorCode.APPOINTMENT_CANCELLED));
    }

    // Chuyển trạng thái lịch hẹn sang đã hoàn thành
    @PutMapping("/{id}/complete")
    @Operation(summary = "Chuyển trạng thái lịch hẹn sang đã hoàn thành", description = "Chuyển trạng thái lịch hẹn sang đã hoàn thành")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    public  ResponseEntity<?> completeAppointment(@PathVariable Long id) {
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
    public  ResponseEntity<?> scheduledAppointment(@PathVariable Long id) {
            appointmentService.scheduledAppointment(id);
            return ResponseEntity.ok("Chuyển trạng thái lịch hẹn sang đã đặt lịch");

    }

}
