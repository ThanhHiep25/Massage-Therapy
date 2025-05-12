package com.example.spa.services;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.DashboardStatsResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.enums.AppointmentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);

    AppointmentResponse getAppointmentById(Long id);

    List<AppointmentResponse> getAllAppointments();

    // Lấy danh sách lịch hẹn theo userid
    List<AppointmentResponse> getAppointmentsByUserId(Long userId);

    Appointment updateAppointment(Long id, AppointmentRequest request);

    void deleteAppointment(Long id);

    // Danh sách lịch hẹn theo trạng thái SCHEDULED
    List<AppointmentResponse> getAllAppointmentsByStatus(String status);

    List<Appointment> getAllAppointmentsByDate(String date);

    // Trạng thái người ùng đang thực hiện việc đặt lịch
    void inProgress(Long id);

    void pendingAppointment(Long id);

    void cancelAppointment(Long id);

    void completeAppointment(Long id);

    void scheduledAppointment(Long id);

    // Thay đổi trang thái lịch hẹn sang thanh toán
    void paidAppointment(Long id);

    // Thống kê tổng số lịch hẹn theo trạng thái
    Map<AppointmentStatus, Long> countAppointmentsByStatus();

    //Thống kê số lượng lịch hẹn trong ngày
    long countAppointmentsToday();

    // Thống kê theo tháng/ năm - tổng số lịch hẹn
    long countAppointmentsByMonth(int year, int month);

    // Thống kê tổng số lượng lịch hẹn
    long countAppointments();

    // Thống kê số lượng đặt và tổng giá của từng dịch vụ
    Map<String, Map<String, Object>> getServiceUsageWithTotalPrice();

    byte[] exportAppointmentsToExcel(List<AppointmentResponse> appointments) throws IOException;
}
