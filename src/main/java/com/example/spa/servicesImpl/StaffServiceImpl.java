package com.example.spa.servicesImpl;

import com.example.spa.dto.request.StaffRequest;
import com.example.spa.entities.Position;
import com.example.spa.entities.Staff;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.PositionRepository;
import com.example.spa.repositories.StaffRepository;
import com.example.spa.services.StaffService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<Staff> getAllStaffs() {
        return staffRepository.findAll();
    }

    @Override
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public Staff createStaff(StaffRequest staffRequest) {
        Position position = positionRepository.findById(staffRequest.getPositionId())
                .orElseThrow(() -> new AppException(ErrorCode.POSITION_INVALID));

        Staff staff = staffRequest.toPartialStaff(position);
        return staffRepository.save(staff);
    }

    @Override
    public Staff updateStaff(Long id, StaffRequest staffRequest) {
        Staff existingStaff = getStaffById(id);
        existingStaff.setName(staffRequest.getName());
        existingStaff.setPhone(staffRequest.getPhone());
        existingStaff.setEmail(staffRequest.getEmail());
        existingStaff.setAddress(staffRequest.getAddress());
        existingStaff.setImageUrl(staffRequest.getImageUrl());
        existingStaff.setDescription(staffRequest.getDescription());
        existingStaff.setStatus(staffRequest.getStatus());

        if (staffRequest.getStartDate() != null && !staffRequest.getStartDate().isEmpty()) {
            existingStaff.setStartDate(LocalDate.parse(staffRequest.getStartDate(), DateTimeFormatter.ISO_DATE));
        }

        Position position = positionRepository.findById(staffRequest.getPositionId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_INVALID));
        existingStaff.setPosition(position);

        return staffRepository.save(existingStaff);
    }

    @Override
    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        staffRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Staff> importStaffsFromJson(String json) {
        try {
            List<StaffRequest> staffRequests = objectMapper.readValue(json, new TypeReference<>() {});

            // Lấy tất cả vị trí để tránh truy vấn nhiều lần
            List<Long> positionIds = staffRequests.stream()
                    .map(StaffRequest::getPositionId)
                    .distinct()
                    .toList();

            Map<Long, Position> positionMap = positionRepository.findAllById(positionIds)
                    .stream()
                    .collect(Collectors.toMap(Position::getPositionId, p -> p));

            return staffRequests.stream()
                    .map(req -> {
                        Position position = positionMap.get(req.getPositionId());
                        if (position == null) {
                            throw new AppException(ErrorCode.ROLE_INVALID);
                        }
                        return staffRepository.save(req.toPartialStaff(position));
                    })
                    .toList();

        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    @Override
    @Transactional
    public List<Staff> importStaffsFromFile(MultipartFile file) {
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            return importStaffsFromJson(json);
        } catch (IOException e) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }
}
