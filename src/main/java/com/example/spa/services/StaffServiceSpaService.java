package com.example.spa.services;

import com.example.spa.dto.request.StaffServiceRequest;
import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.Staff;
import com.example.spa.entities.StaffServiceSpa;
import com.example.spa.enums.StaffServiceStatus;

import java.util.List;
import java.util.Map;

public interface StaffServiceSpaService {

    StaffServiceResponse assignStaffToService(StaffServiceRequest request);

    List<StaffServiceResponse> getStaffByService(Long serviceId);

    List<StaffServiceSpa> getServicesByStaff(Long staffId);

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

    //Thống kê số lượng phân công nhân viên - dịch vụ
    long countAllStaffServices();

    // Thống kê số lượng phân công theo trạng thái
    long countStaffServicesByStatus(StaffServiceStatus status);

    // Thống kê số lượng dịch vụ được giao cho một nhân viên
    long countServiceByStaffId(Long staffId);

    // Thống kê số lượng nhân viên giao dịch vụ
    long countStaffByServiceId(Long serviceId);

    // Thống kê số lượng dịch vụ được giao cho mỗi nhân viên
    Map<Staff, Long> countServicesAssignedToAllStaff();
}