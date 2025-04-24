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

import com.example.spa.entities.StaffServiceSpa;
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
    private ServiceSpaResponse service;
    private LocalDate assignedDate;
    private String note;
    private String status;

    public StaffServiceResponse(StaffServiceSpa entity) {
        this.id = entity.getId();
        this.staff = new StaffResponse(entity.getStaff());
        this.service = new ServiceSpaResponse(entity.getServiceSpa(), entity.getServiceSpa().getSteps());
        this.assignedDate = entity.getAssignedDate();
        this.note = entity.getNote();
        this.status = entity.getStatus().name();
    }
}


