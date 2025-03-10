package com.example.spa.servicesImpl;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.entities.Appointment;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.Staff;
import com.example.spa.entities.User;
import com.example.spa.enums.AppointmentStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.AppointmentRepository;
import com.example.spa.repositories.ServiceSpaRepository;
import com.example.spa.repositories.StaffRepository;
import com.example.spa.repositories.UserRepository;
import com.example.spa.services.AppointmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ServiceSpaRepository serviceSpaRepository;

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentRequest request) {
        // Tìm user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        // Tìm nhân viên
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        // Tìm danh sách dịch vụ theo ID
        List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
        }

        // Tính tổng giá tiền từ danh sách dịch vụ (khắc phục lỗi BigDecimal::add)
        BigDecimal totalPrice = services.stream()
                .map(service -> BigDecimal.valueOf(service.getPrice())) // Chuyển double → BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double totalPriceDouble = totalPrice.doubleValue();


        // Tạo lịch hẹn
        Appointment appointment = Appointment.builder()
                .user(user)
                .staff(staff)
                .appointmentDateTime(request.getAppointmentDateTime())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.COMPLETED)
                .totalPrice(totalPrice)
                .services(services) // Gán danh sách dịch vụ vào lịch hẹn
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch hẹn không tồn tại"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
        }

        BigDecimal totalPrice = services.stream()
                .map(service -> BigDecimal.valueOf(service.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        appointment.setUser(user);
        appointment.setStaff(staff);
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING);
        appointment.setTotalPrice(totalPrice);
        appointment.setServices(services);

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID);
        }
        appointmentRepository.deleteById(id);
    }


    @Override
    public List<Appointment> getAllAppointmentsByStatus(String status) {
        return appointmentRepository.findAllByStatus(AppointmentStatus.valueOf(status));
    }

    @Override
    public List<Appointment> getAllAppointmentsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return appointmentRepository.findByAppointmentDateTimeBetween(
                localDate.atStartOfDay(), localDate.plusDays(1).atStartOfDay());
    }

}
