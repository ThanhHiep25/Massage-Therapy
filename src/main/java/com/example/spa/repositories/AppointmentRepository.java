package com.example.spa.repositories;

import com.example.spa.entities.Appointment;
import com.example.spa.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByStatus(AppointmentStatus appointmentStatus);

    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

}
