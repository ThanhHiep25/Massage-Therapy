package com.example.spa.services;

import com.example.spa.dto.request.StaffServiceRequest;
import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.StaffAppointment;

import java.util.List;

public interface StaffServiceSpaService {

    StaffServiceResponse assignStaffToService(StaffServiceRequest request);

    List<StaffServiceResponse> getStaffByService(Long serviceId);

    List<StaffAppointment> getServicesByStaff(Long staffId);

    void deleteStaffService(Long id);

    void updateStaffService(Long id, StaffServiceRequest request);

    StaffServiceResponse getStaffServiceById(Long id);

    List<StaffServiceResponse> getAllStaffServices();

    void Unassigned(Long id);

    void Assigning(Long id);

    void Assigned(Long id);

    void InProgress(Long id);

    void Completed(Long id);

    void cancelled(Long id);

    void Approval(Long id);

    void Overdue(Long id);

}