package com.example.spa.repositories;

import com.example.spa.entities.Appointment;
import com.example.spa.enums.AppointmentStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Tim kiem theo trang thai
    List<Appointment> findAllByStatus(AppointmentStatus appointmentStatus);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    // Dem theo trang thai
    Long countByStatus(AppointmentStatus status);

    List<Appointment> findByUser_UserId(Long userId);

    // Lich hen trong ngay
    long countByAppointmentDateTimeBetween(LocalDateTime startOfToday, LocalDateTime endOfToday);

    // Query để kiểm tra xem có lịch hẹn nào sử dụng ServiceSpa với ID cho trước hay không
    @Query(value = "SELECT COUNT(*) FROM appointment_services WHERE service_id = :serviceSpaId", nativeQuery = true)
    long existsAppointmentWithServiceSpaId(@Param("serviceSpaId") Long serviceSpaId);
}
