package com.example.spa.servicesImpl;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.entities.Appointment;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.Staff;
import com.example.spa.entities.User;
import com.example.spa.enums.AppointmentStatus;
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
                .build();

        return appointmentRepository.save(appointment);
    }


}
