
package com.example.spa.services;

import com.example.spa.dto.request.StaffRequest;
import com.example.spa.entities.Staff;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StaffService {
    List<Staff> getAllStaffs();
    Staff getStaffById(Long id);
    Staff createStaff(StaffRequest staffRequest);
    Staff updateStaff(Long id, StaffRequest staffRequest);
    void deleteStaff(Long id);
    List<Staff> importStaffsFromJson(String json);
    List<Staff> importStaffsFromFile(MultipartFile file) throws IOException;
}
