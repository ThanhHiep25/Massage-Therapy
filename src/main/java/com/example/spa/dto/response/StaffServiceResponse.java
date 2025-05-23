package com.example.spa.dto.response;
//
//import com.example.spa.entities.StaffServiceSpa;
//import lombok.*;
//
//import java.time.LocalDate;
//
//@Setter
//@Getter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class StaffServiceResponse {
//    private Long id;
//    private Long staffId;
//    private Long serviceId;
//    private String staffName;
//    private String serviceName;
//    private LocalDate assignedDate;
//    private String note;
//    private String status;
//
//    public StaffServiceResponse (StaffServiceSpa entity) {
//        this.id = entity.getId();
//        this.staffId = entity.getStaff().getStaffId();
//        this.serviceId = entity.getServiceSpa().getServiceId();
//        this.staffName = entity.getStaff().getName();
//        this.serviceName = entity.getServiceSpa().getName();
//        this.assignedDate = entity.getAssignedDate();
//        this.note = entity.getNote();
//        this.status = entity.getStatus().name();
//    }
//
//}

import com.example.spa.entities.StaffAppointment;
import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffServiceResponse {
    private Long id;
    private StaffResponse staff;
    private AppointmentResponse appointment;
    private DepartmentResponse department;
    private LocalDate assignedDate;
    private String note;
    private String status;

    public StaffServiceResponse(StaffAppointment entity) {
        this.id = entity.getId();
        this.staff = new StaffResponse(entity.getStaff());
        this.appointment = new AppointmentResponse(entity.getAppointment());
        this.department = new DepartmentResponse(entity.getDepartment());
        this.assignedDate = entity.getAssignedDate();
        this.note = entity.getNote();
        this.status = entity.getStatus().name();
    }
}


