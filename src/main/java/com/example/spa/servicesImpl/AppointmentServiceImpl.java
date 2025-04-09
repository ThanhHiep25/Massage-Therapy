package com.example.spa.servicesImpl;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.dto.response.UserResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.entities.ServiceSpa;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ServiceSpaRepository serviceSpaRepository;

//    @Override
//    @Transactional
//    public Appointment createAppointment(AppointmentRequest request) {
//        // Tìm user
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
//
//        // Tìm nhân viên
////        Staff staff = staffRepository.findById(request.getStaffId())
////                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));
//
//        // Tìm danh sách dịch vụ theo ID
//        List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
//        if (services.isEmpty()) {
//            throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
//        }
//
//        // Tổng giá tiền từ danh sách dịch vụ (khắc phục lỗi BigDecimal::add)
//        BigDecimal totalPrice = services.stream()
//                .map(service -> BigDecimal.valueOf(service.getPrice())) // Chuyển double → BigDecimal
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Tạo lịch hẹn
//        Appointment appointment = Appointment.builder()
//                .user(user)
////                .staff(staff)
//                .appointmentDateTime(request.getAppointmentDateTime())
//                .notes(request.getNotes())
//                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING)
//                .totalPrice(totalPrice)
//                .services(services) // Gán danh sách dịch vụ vào lịch hẹn
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        return appointmentRepository.save(appointment);
//    }

@Override
@Transactional
public AppointmentResponse createAppointment(AppointmentRequest request) {
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

    List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
    if (services.isEmpty()) {
        throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
    }

    BigDecimal totalPrice = request.getTotalPrice();
    if (totalPrice == null) {
        throw new IllegalArgumentException("Tổng giá tiền không được để trống");
    }

    Appointment appointment = Appointment.builder()
            .user(user)
            .appointmentDateTime(request.getAppointmentDateTime())
            .notes(request.getNotes())
            .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING)
            .totalPrice(totalPrice)
            .services(services)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Appointment saved = appointmentRepository.save(appointment);

    // Chuyển sang AppointmentResponse
    return new AppointmentResponse(saved);
}


    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        try {
            return appointmentRepository.findAll().stream().map(appointment -> {
                return AppointmentResponse.builder()
                        .id(appointment.getAppointmentId())
                        .userId(new UserResponse(appointment.getUser()))
                        .appointmentDateTime(appointment.getAppointmentDateTime())
                        .totalPrice(appointment.getTotalPrice())
                        .notes(appointment.getNotes())
                        .status(appointment.getStatus())
                        .serviceIds(
                                appointment.getServices().stream()
                                        .map(serviceSpa -> new ServiceSpaResponse(
                                                serviceSpa,
                                                serviceSpa.getSteps() != null ? serviceSpa.getSteps() : Collections.emptyList()
                                        ))
                                        .collect(Collectors.toList())
                        )
                        .createdAt(appointment.getCreatedAt())
                        .updatedAt(appointment.getUpdatedAt())
                        .build();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID);
        }
    }


    @Override
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch hẹn không tồn tại"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

//        Staff staff = staffRepository.findById(request.getStaffId())
//                .orElseThrow(() -> new EntityNotFoundException("Nhân viên không tồn tại"));

        List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
        }

        BigDecimal totalPrice = services.stream()
                .map(service -> BigDecimal.valueOf(service.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        appointment.setUser(user);
//        appointment.setStaff(staff);
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

    @Override
    public void pendingAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void scheduledAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
    }

}
