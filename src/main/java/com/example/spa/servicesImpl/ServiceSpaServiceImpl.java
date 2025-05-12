package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ServiceSpaRequest;
import com.example.spa.dto.response.ServiceSpaResponse;
import com.example.spa.entities.Categories;
import com.example.spa.entities.ServiceSpa;
import com.example.spa.entities.ServiceSpaImage;
import com.example.spa.entities.ServiceStep;
import com.example.spa.enums.StatusBasic;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.*;
import com.example.spa.services.ServiceSpaService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ServiceSpaServiceImpl implements ServiceSpaService {

    private final ServiceSpaRepository serviceSpaRepository;
    private final ServiceStepRepository serviceStepRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceSpaImageRepository serviceSpaImageRepository;
    private final AppointmentRepository appointmentRepository;
    private final ObjectMapper objectMapper;

    // Tạo ServiceSpa
    @Override
    @Transactional
    public ServiceSpa createServiceSpa(ServiceSpaRequest serviceSpaRequest, List<ServiceStep> steps) {
        Categories category = categoryRepository.findById(serviceSpaRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if(serviceSpaRepository.existsByName(serviceSpaRequest.getName())){
            throw new AppException(ErrorCode.SERVICE_ALREADY_EXISTED);
        }

        ServiceSpa newService = serviceSpaRequest.toServiceSpa(category);
        ServiceSpa savedService = serviceSpaRepository.save(newService);

        // Liên kết danh sách ServiceStep với ServiceSpa
        if (steps != null && !steps.isEmpty()) {
            steps.forEach(step -> step.setServiceSpa(savedService));
            savedService.getSteps().addAll(steps); // Gán steps vào ServiceSpa
            serviceStepRepository.saveAll(steps);
        }

        //  Lưu danh sách hình ảnh nếu có
        if (serviceSpaRequest.getImageUrls() != null && !serviceSpaRequest.getImageUrls().isEmpty()) {
            List<ServiceSpaImage> images = serviceSpaRequest.getImageUrls().stream()
                    .map(url -> ServiceSpaImage.builder()
                            .imageUrl(url)
                            .serviceSpa(savedService)
                            .build())
                    .toList();
            serviceSpaImageRepository.saveAll(images);
            savedService.getImages().addAll(images); // Gán images vào ServiceSpa
        }

        return savedService;
    }


    // Lấy ServiceSpa theo id
    @Override
    public ServiceSpa getServiceSpaById(Long id) {
        return serviceSpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ServiceSpa không tồn tại"));
    }

    // Lấy danh sách ServiceSpa
    @Override
    public List<ServiceSpa> getAllServiceSpas() {
        return serviceSpaRepository.findAll();
    }

    // Xoa ServiceSpa
//    @Override
//    @Transactional
//    public void deleteServiceSpa(Long id) {
//        if (!serviceSpaRepository.existsById(id)) {
//            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
//        }
//        serviceSpaRepository.deleteById(id);
//    }

    @Override
    public void deleteServiceSpa(Long id) {
        if (!serviceSpaRepository.existsById(id)) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }

        // Kiểm tra xem có lịch hẹn nào sử dụng ServiceSpa này hay không
        if (appointmentRepository.existsAppointmentWithServiceSpaId(id) > 0) {
            throw new AppException(ErrorCode.APPOINTMENT_ALREADY_EXISTED);
        }

        serviceSpaRepository.deleteById(id);
    }

    // Cap nhap ServiceSpa
    @Override
    @Transactional
    public ServiceSpa updateServiceSpa(Long id, ServiceSpaRequest request, List<ServiceStep> steps) {
        ServiceSpa existingService = getServiceSpaById(id);

        // Update category if provided
        if (request.getCategoryId() != null) {
            Categories category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existingService.setCategories(category);
        }

        // Update basic fields
        existingService.setName(request.getName());
        existingService.setDescription(request.getDescription());
        existingService.setPrice(request.getPrice());
        existingService.setDuration(request.getDuration());
        existingService.setService_type(request.getServiceType());

        // Update steps
        if (steps != null) {
            // Remove steps that are not in the new list
            List<ServiceStep> existingSteps = serviceStepRepository.findByServiceSpa(existingService);
            List<Long> newStepIds = steps.stream()
                    .map(ServiceStep::getStep_id)
                    .collect(Collectors.toList());
            existingSteps.stream()
                    .filter(step -> !newStepIds.contains(step.getStep_id()))
                    .forEach(serviceStepRepository::delete);

            // Update or add new steps
            for (ServiceStep step : steps) {
                if (step.getStep_id() != null) {
                    ServiceStep existingStep = serviceStepRepository.findById(step.getStep_id())
                            .orElseThrow(() -> new AppException(ErrorCode.STEP_NOT_FOUND));
                    existingStep.setStepOrder(step.getStepOrder());
                    existingStep.setDescription(step.getDescription());
                    serviceStepRepository.save(existingStep);
                } else {
                    step.setServiceSpa(existingService);
                    serviceStepRepository.save(step);
                }
            }
        }


        return serviceSpaRepository.save(existingService);
    }


    // Import ServiceSpa tu file
    @Override
    @Transactional
    public Map<String, Object> importServiceSpasFromFile(MultipartFile file) {
        try {
            // Đọc JSON từ file thành danh sách `ServiceSpaRequest`
            String jsonContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<ServiceSpaRequest> requests = objectMapper.readValue(
                    jsonContent,
                    new TypeReference<List<ServiceSpaRequest>>() {}
            );

            // Gọi phương thức importServiceSpas để xử lý và lưu dữ liệu
            return importServiceSpas(requests);

        } catch (IOException e) {
            // Xử lý lỗi nếu file không hợp lệ
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Lỗi đọc file JSON: " + e.getMessage());
            return errorResponse;
        }
    }



// Import ServiceSpa tu danh sách
    @Override
    @Transactional
    public Map<String, Object> importServiceSpas(List<ServiceSpaRequest> requests) {
        List<String> successMessages = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        // Duyệt qua danh sách `ServiceSpaRequest` và tạo `ServiceSpa`
        for (ServiceSpaRequest request : requests) {
            try {
                Categories category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

                ServiceSpa newService = request.toServiceSpa(category);
                serviceSpaRepository.save(newService);

                successMessages.add("Thành công: " + request.getName());
            } catch (Exception e) {
                errorMessages.add("Lỗi: " + request.getName() + " - " + e.getMessage());
            }
        }

        // Trả về Map chứa cả thành công và lỗi
        Map<String, Object> response = new HashMap<>();
        response.put("status", errorMessages.isEmpty() ? "success" : "error");
        response.put("success", successMessages);
        response.put("errors", errorMessages);

        return response;
    }



    // Xóa tất cả ServiceSpa
    @Override
    public void deleteAllServiceSpas() {
        serviceSpaRepository.deleteAll();
    }

    // Lấy ServiceSpa theo tên
    @Override
    public ServiceSpa findByServiceName(String serviceName) {
        return serviceSpaRepository.findByName(serviceName);
    }

    // Kiem tra ServiceSpa theo tên
    @Override
    public boolean existsByServiceName(String serviceName) {
        return serviceSpaRepository.existsByName(serviceName);
    }

    // Set trạng thái ngừng hoạt động
    @Override
    public void deactivateServiceSpa(Long id) {
        try {
            ServiceSpa serviceSpa = serviceSpaRepository.findById(id).orElseThrow(() ->
                    new AppException(ErrorCode.SERVICE_NOT_FOUND));
            serviceSpa.setStatus(StatusBasic.DEACTIVATED);
            serviceSpaRepository.save(serviceSpa);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_INVALID);
        }
    }

    // Set trạng thái hóat động
    @Override
    public void activateServiceSpa(Long id) {
        try {
            ServiceSpa serviceSpa = serviceSpaRepository.findById(id).orElseThrow(() ->
                    new AppException(ErrorCode.SERVICE_NOT_FOUND));
            serviceSpa.setStatus(StatusBasic.ACTIVATE);
            serviceSpaRepository.save(serviceSpa);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_INVALID);
        }
    }

    //Lấy tổng số lượng dịch vụ spa
    @Override
    public Long countServiceSpa() {
        return serviceSpaRepository.count();
    }

    //Thống kê số lượng dịch vụ theo danh mục
    @Override
    public Map<String, Long> getServicesByCategory() {
        return serviceSpaRepository.findAll().stream()
                .collect(Collectors.groupingBy(service -> service.getCategories().getCategoryName(), Collectors.counting()));
    }

    //Thống kê số lượng dịch vụ theo danh mục (trả về name và value)
    @Override
    public List<Map.Entry<String, Long>> getServicesByCategoryWithNameValue() {
        return serviceSpaRepository.findAll().stream()
                .collect(Collectors.groupingBy(service -> service.getCategories().getCategoryName(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }



    // Xuat file excel
    @Override
    public byte[] exportServiceSpasToExcel(List<ServiceSpaResponse> serviceSpas) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dịch vụ Spa");

        // Style cho header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);

        // Style cho data
        CellStyle dataStyle = workbook.createCellStyle();
        setBorder(dataStyle);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Tên dịch vụ", "Mô tả", "Giá", "Thời lượng (phút)", "ID Danh mục", "Hình ảnh", "Loại dịch vụ", "Các bước", "Trạng thái"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DecimalFormat priceFormatter = new DecimalFormat("#,##0.00");

        // Create data rows
        int rowNum = 1;
        for (ServiceSpaResponse serviceSpa : serviceSpas) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, serviceSpa.getId(), dataStyle);
            createCell(row, 1, serviceSpa.getName(), dataStyle);
            createCell(row, 2, serviceSpa.getDescription(), dataStyle);
            createCell(row, 3, priceFormatter.format(serviceSpa.getPrice()), dataStyle);
            createCell(row, 4, serviceSpa.getDuration(), dataStyle);
            createCell(row, 5, serviceSpa.getCategoryId(), dataStyle);
            createCell(row, 6, String.join(", ", serviceSpa.getImages()), dataStyle); // Nối danh sách ảnh thành chuỗi
            createCell(row, 7, serviceSpa.getServiceType(), dataStyle);
            createCell(row, 8, serviceSpa.getSteps().stream()
                    .map(step -> String.format("%d. %s", step.getStepOrder(), step.getDescription()))
                    .collect(Collectors.joining("\n")), dataStyle); // Nối tên các bước
            createCell(row, 9, serviceSpa.getStatus(), dataStyle);
        }

        // Auto size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }

    private void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

}
