package com.example.spa.repositories;

import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.StaffAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffServiceSpaRepository extends JpaRepository<StaffAppointment, Long> {

    List<StaffAppointment> findByStaffStaffId(Long staffId);

    boolean existsByStaffStaffId(Long id);

    List<StaffServiceResponse> findByAppointmentAppointmentId(Long serviceId);

    List<StaffAppointment> findByAppointment_AppointmentIdAndDepartment_DepartmentId(Long appointmentId, Long departmentId);

    List<StaffAppointment> findByAppointment_AppointmentId(Long appointmentId);
}