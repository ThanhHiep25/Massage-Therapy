package com.example.spa.servicesImpl;


import com.example.spa.dto.request.StaffServiceRequest;
import com.example.spa.dto.response.StaffServiceResponse;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.Staff;
import com.example.spa.entities.StaffServiceSpa;
import com.example.spa.enums.StaffServiceStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.ServiceSpaRepository;
import com.example.spa.repositories.StaffRepository;
import com.example.spa.repositories.StaffServiceSpaRepository;
import com.example.spa.services.StaffServiceSpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceSpaServiceImpl implements StaffServiceSpaService {

    private final StaffRepository staffRepository;

    private final ServiceSpaRepository serviceSpaRepository;

    private final StaffServiceSpaRepository staffServiceSpaRepository;



    // 1. Phân công nhân viên vào dịch vụ
//    @Override
//    @Transactional
//    public StaffServiceResponse assignStaffToService(StaffServiceRequest request) {
//        Staff staff = staffRepository.findById(request.getStaffId())
//                .orElseThrow(() -> new RuntimeException("Staff not found"));
//
//        ServiceSpa serviceSpa = serviceSpaRepository.findById(request.getServiceId())
//                .orElseThrow(() -> new RuntimeException("Service not found"));
//
//        StaffServiceSpa staffServiceSpa = StaffServiceSpa.builder()
//                .staff(staff)
//                .serviceSpa(serviceSpa)
//                .assignedDate(request.getAssignedDate())
//                .note(request.getNote())
//                .status(request.getStatus() != null ? request.getStatus() : StaffServiceStatus.Unassigned)
//                .build();
//
//        StaffServiceSpa saved = staffServiceSpaRepository.save(staffServiceSpa);
//
//        return new StaffServiceResponse(saved); // <-- dùng constructor của bạn
//    }

    @Override
    @Transactional
    public StaffServiceResponse assignStaffToService(StaffServiceRequest request) {
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        ServiceSpa serviceSpa = serviceSpaRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Kiểm tra xem đã có phân công này chưa
        boolean exists = staffServiceSpaRepository.existsByStaffAndServiceSpa(staff, serviceSpa);
        if (exists) {
            throw new AppException(ErrorCode.STAFF_SERVICE_ALREADY_EXISTED);
        }

        StaffServiceSpa staffServiceSpa = StaffServiceSpa.builder()
                .staff(staff)
                .serviceSpa(serviceSpa)
                .assignedDate(request.getAssignedDate())
                .note(request.getNote())
                .status(request.getStatus() != null ? request.getStatus() : StaffServiceStatus.Unassigned)
                .build();

        StaffServiceSpa saved = staffServiceSpaRepository.save(staffServiceSpa);

        return new StaffServiceResponse(saved);
    }



    // 2. Lấy danh sách nhân viên theo dịch vụ
    @Override
    public List<StaffServiceResponse> getStaffByService(Long serviceId) {
        return staffServiceSpaRepository.findByServiceSpaServiceId(serviceId);
    }

    // 3. Lấy danh sách dịch vụ mà nhân viên đang làm
    @Override
    public List<StaffServiceSpa> getServicesByStaff(Long staffId) {
        return staffServiceSpaRepository.findByStaffStaffId(staffId);
    }


    @Override
    public void deleteStaffService(Long id) {
        StaffServiceSpa staffServiceSpa = staffServiceSpaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_SERVICE_NOT_FOUND));
        staffServiceSpaRepository.delete(staffServiceSpa);
    }


    @Override
    @Transactional
    public void updateStaffService(Long id, StaffServiceRequest request) {
        StaffServiceSpa staffServiceSpa = staffServiceSpaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_SERVICE_NOT_FOUND));

        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_EXISTED));

        ServiceSpa serviceSpa = serviceSpaRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        staffServiceSpa.setStaff(staff);
        staffServiceSpa.setServiceSpa(serviceSpa);
        staffServiceSpa.setAssignedDate(request.getAssignedDate());

        staffServiceSpaRepository.save(staffServiceSpa);
    }


    @Override
    public StaffServiceResponse getStaffServiceById(Long id) {
        // Tìm kiếm StaffServiceSpa theo ID
        StaffServiceSpa staffServiceSpa = staffServiceSpaRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_SERVICE_NOT_FOUND));

        return new StaffServiceResponse(staffServiceSpa);
    }



    @Override
    @Transactional
    public List<StaffServiceResponse> getAllStaffServices() {
        // Lấy tất cả StaffServiceSpa và chuyển đổi thành StaffServiceResponse
        return staffServiceSpaRepository.findAll().stream()
                .map(StaffServiceResponse::new) // Chuyển đổi mỗi StaffServiceSpa thành StaffServiceResponse
                .collect(Collectors.toList()); // Thu thập thành danh sách
    }


    @Override
    public void Unassigned(Long id) {

    }

    @Override
    public void Assigning(Long id) {

    }

    @Override
    public void Assigned(Long id) {

    }

    @Override
    public void InProgress(Long id) {

    }

    @Override
    public void Completed(Long id) {

    }

    @Override
    public void cancelled(Long id) {

    }

    @Override
    public void Approval(Long id) {

    }

    @Override
    public void Overdue(Long id) {

    }
}
