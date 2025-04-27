package com.example.spa.repositories;

import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.Appointment;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.Staff;
import com.example.spa.entities.StaffServiceSpa;
import com.example.spa.enums.StaffServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffServiceSpaRepository extends JpaRepository<StaffServiceSpa, Long> {

    List<StaffServiceSpa> findByStaffStaffId(Long staffId);

    boolean existsByStaffStaffId(Long id);

    List<StaffServiceResponse> findByAppointmentAppointmentId(Long serviceId);
}