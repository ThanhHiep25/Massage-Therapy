package com.example.spa.servicesImpl;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.*;
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
import com.example.spa.services.MailService;
import com.example.spa.services.NotificationService;
import com.example.spa.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ServiceSpaRepository serviceSpaRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    private final MailService mailService;


    // Tạo lịch hẹn
    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {

        // Lấy dịch vụ
        List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
        if (services.isEmpty()) {
            throw new IllegalArgumentException("Không có dịch vụ hợp lệ được chọn");
        }

        BigDecimal totalPrice = services.stream()
                .map(service -> BigDecimal.valueOf(service.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        if (totalPrice == null) {
//            throw new IllegalArgumentException("Tổng giá tiền không được để trống");
//        }

        Appointment.AppointmentBuilder appointmentBuilder = Appointment.builder()
                .appointmentDateTime(request.getAppointmentDateTime() != null
                        ? request.getAppointmentDateTime()
                        : LocalDateTime.now())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING)
                .totalPrice(totalPrice)
                .services(services)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());

        // Nếu có userId thì gán User
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
            appointmentBuilder.user(user);
        } else {
            // Trường hợp khách vãng lai
            String guestName = (request.getGuestName() == null || request.getGuestName().trim().isEmpty())
                    ? "Khách hàng ẩn danh"
                    : request.getGuestName();

            appointmentBuilder
                    .guestName(guestName);

        }
//        notificationService.sendAppointmentNotification(new AppointmentResponse(appointmentBuilder.build()));
//
//        notificationService.sendAppointmentNotificationCustomer();

        Appointment saved = appointmentRepository.save(appointmentBuilder.build());
        return new AppointmentResponse(saved);
    }


    // Lấy lịch hẹn theo id
    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        return new AppointmentResponse(appointment);
    }


    // Lấy danh sách lịch hẹn
    @Override
    public List<AppointmentResponse> getAllAppointments() {
        try {
            return appointmentRepository.findAll().stream().map(appointment -> {
                // Nếu không có user, trả về null
                UserResponse userResponse = appointment.getUser() != null
                        ? new UserResponse(appointment.getUser())
                        : null;

                return AppointmentResponse.builder()
                        .id(appointment.getAppointmentId())
                        .userId(userResponse) // Trả về null nếu không có user
                        .appointmentDateTime(appointment.getAppointmentDateTime())
                        .gustName(appointment.getGuestName())
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

    // Lấy danh sách lịch hẹn theo userid
    @Override
    public List<AppointmentResponse> getAppointmentsByUserId(Long userId) {
        try {
            return appointmentRepository.findByUser_UserId(userId).stream().map(appointment -> {
                return AppointmentResponse.builder()
                        .id(appointment.getAppointmentId())
                        .userId(new UserResponse(appointment.getUser()))
                        .appointmentDateTime(appointment.getAppointmentDateTime())
                        .gustName(appointment.getGuestName())
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


    // Cập nhật lịch hẹn
    @Override
    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lịch hẹn không tồn tại"));

        // Xử lý trường hợp khách hàng đã có tài khoản
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));
            appointment.setUser(user);
            appointment.setGuestName(null); // Nếu có userId, thì không phải khách vãng lai
        } else {
            // Xử lý trường hợp khách vãng lai
            appointment.setUser(null);
            if (request.getGuestName() != null && !request.getGuestName().trim().isEmpty()) {
                appointment.setGuestName(request.getGuestName());
            } else if (appointment.getGuestName() == null) {
                appointment.setGuestName("Khách hàng ẩn danh"); // Đảm bảo có guestName nếu không có userId
            }
        }

        if (request.getAppointmentDateTime() != null) {
            appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }

        if (request.getServiceIds() != null) {
            List<ServiceSpa> services = serviceSpaRepository.findAllById(request.getServiceIds());
            if (!services.isEmpty()) {
                appointment.setServices(services);
                BigDecimal totalPrice = services.stream()
                        .map(service -> BigDecimal.valueOf(service.getPrice()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                appointment.setTotalPrice(totalPrice);
            } else {
                appointment.setServices(Collections.emptyList());
                appointment.setTotalPrice(BigDecimal.ZERO);
            }
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    // Xóa lịch hẹn
    @Override
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID);
        }
        appointmentRepository.deleteById(id);
    }

    // Lịch hẹn theo trang thái
    // Lịch hẹn theo trang thái
    @Override
    public List<AppointmentResponse> getAllAppointmentsByStatus(String status) {
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());

            return appointmentRepository.findAllByStatus(appointmentStatus).stream()
                    .map(appointment -> {
                        // Nếu không có user, trả về null
                        UserResponse userResponse = appointment.getUser() != null
                                ? new UserResponse(appointment.getUser())
                                : null;

                        return AppointmentResponse.builder()
                                .id(appointment.getAppointmentId())
                                .userId(userResponse) // Trả về null nếu không có user
                                .gustName(appointment.getGuestName())
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
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID);
        }
    }

    // Lịch hẹn theo ngày
    @Override
    public List<Appointment> getAllAppointmentsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return appointmentRepository.findByAppointmentDateTimeBetween(
                localDate.atStartOfDay(), localDate.plusDays(1).atStartOfDay());
    }

    // Trạng thái người ùng đang thực hiện việc đặt lịch
    @Override
    public void inProgress(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointmentRepository.save(appointment);
    }

    // Thay đổi trang thái lịch hẹn sang chờ đặt lịch
    @Override
    public void pendingAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
    }

    // Thay đổi trang thái lịch hẹn sang hủy
    @Override
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        AppointmentResponse response = new AppointmentResponse(appointment);
        if (appointment.getUser() != null && appointment.getUser().getEmail() != null) {
            String email = appointment.getUser().getEmail();
            mailService.CANCELAPPOINTMENT(email);
        }

        appointmentRepository.save(appointment);
    }

    // Thay đổi trang thái lịch hẹn sang hoàn thành
    @Override
    public void completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }

    // Thay đổi trang thái lịch hẹn sang đặt lịch
    @Override
    public void scheduledAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Gửi mail sau khi cập nhật trạng thái cho khách hàng
        AppointmentResponse response = new AppointmentResponse(appointment);
        if (appointment.getUser() != null && appointment.getUser().getEmail() != null) {
            String email = appointment.getUser().getEmail();
            mailService.sendMailSCHEDULED(email, response);
        }
        appointmentRepository.save(appointment);
    }

    // Thay đổi trang thái lịch hẹn sang thanh toán
    @Override
    public void paidAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_INVALID));
        appointment.setStatus(AppointmentStatus.PAID);
        appointmentRepository.save(appointment);
    }

    // Thống kê tổng số lịch hẹn theo trạng thái
    @Override
    public Map<AppointmentStatus, Long> countAppointmentsByStatus() {
        return Arrays.stream(AppointmentStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        appointmentRepository::countByStatus
                ));
    }

    // Dem so lich hen trong ngay
    @Override
    public long countAppointmentsToday() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);
        return appointmentRepository.countByAppointmentDateTimeBetween(startOfToday, endOfToday);
    }

    // Thống kê số lượng lịch hẹn theo tháng, năm
    @Override
    public long countAppointmentsByMonth(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        return appointmentRepository.countByAppointmentDateTimeBetween(start, end);
    }

    // Thống kê tổng số lượng lịch hẹn
    @Override
    public long countAppointments() {
        return appointmentRepository.count();
    }

    // Thống kê số lượng đặt và tổng giá của từng dịch vụ
    @Override
    public Map<String, Map<String, Object>> getServiceUsageWithTotalPrice() {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        Map<String, Map<String, Object>> serviceUsage = new HashMap<>();

        for (Appointment appointment : allAppointments) {
            for (ServiceSpa service : appointment.getServices()) {
                String serviceName = service.getName();
                double servicePrice = service.getPrice();

                if (serviceUsage.containsKey(serviceName)) {
                    Map<String, Object> serviceData = serviceUsage.get(serviceName);
                    long count = (long) serviceData.get("count");
                    double totalPrice = (double) serviceData.get("totalPrice");
                    serviceData.put("count", count + 1);
                    serviceData.put("totalPrice", totalPrice + servicePrice);
                } else {
                    Map<String, Object> serviceData = new HashMap<>();
                    serviceData.put("count", 1L);
                    serviceData.put("totalPrice", servicePrice);
                    serviceUsage.put(serviceName, serviceData);
                }
            }
        }
        return serviceUsage;
    }

    @Override
    public byte[] exportAppointmentsToExcel(List<AppointmentResponse> appointments) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Appointments");

        // Style cho header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);

        // Style cho data
        CellStyle dataStyle = workbook.createCellStyle();
        setBorder(dataStyle);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã lịch hẹn ", "Người dùng", "Khách hàng", "Thời gian hẹn", "Tổng tiền", "Ghi chú", "Trạng thái", "Dịch vụ", "Ngày tạo", "Ngày cập nhật"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (AppointmentResponse appointment : appointments) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, appointment.getId(), dataStyle);
            createCell(row, 1, appointment.getUserId() != null ? appointment.getUserId().getName() : "Trống", dataStyle);
            createCell(row, 2, appointment.getGustName() != null ? appointment.getGustName() : "Trống", dataStyle);
            createCell(row, 3, appointment.getAppointmentDateTime() != null ? appointment.getAppointmentDateTime().format(formatter) : "", dataStyle);
            createCell(row, 4, appointment.getTotalPrice() != null ? appointment.getTotalPrice().toString() : "0.0", dataStyle);
            createCell(row, 5, appointment.getNotes() != null ? appointment.getNotes() : "Trống", dataStyle);
            createCell(row, 6, appointment.getStatus() != null ? appointment.getStatus().name() : "", dataStyle);
            createCell(row, 7, appointment.getServiceIds().stream().map(ServiceSpaResponse::getName).collect(Collectors.joining(", ")), dataStyle);
            createCell(row, 8, appointment.getCreatedAt() != null ? appointment.getCreatedAt().format(formatter) : "", dataStyle);
            createCell(row, 9, appointment.getUpdatedAt() != null ? appointment.getUpdatedAt().format(formatter) : "", dataStyle);
        }

        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

}
