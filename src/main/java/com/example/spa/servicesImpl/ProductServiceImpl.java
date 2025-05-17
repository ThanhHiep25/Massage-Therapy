package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ProductRequest;
import com.example.spa.dto.response.CategoryResponse;
import com.example.spa.dto.response.ProductResponse;
import com.example.spa.entities.Categories;
import com.example.spa.entities.Product;
import com.example.spa.enums.ProductStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.CategoryRepository;
import com.example.spa.repositories.OrderItemRepository;
import com.example.spa.repositories.ProductRepository;
import com.example.spa.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoriesRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Categories categoryEntity = categoriesRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // Kiểm tra tên sản phẩm trùng
        if (productRepository.existsByNameProduct(request.getNameProduct())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTED);
        }

        Product product = Product.builder()
                .nameProduct(request.getNameProduct())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(categoryEntity)
                .imageUrl(request.getImageUrl())
                .quantity(request.getQuantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .productStatus(ProductStatus.ACTIVATE)
                .build();
        Product savedProduct = productRepository.save(product);

        // Ánh xạ đối tượng Categories sang CategoryResponse
        CategoryResponse category = CategoryResponse.builder()
                .id(savedProduct.getCategory().getCategoryId())
                .name(savedProduct.getCategory().getCategoryName())
                .build();

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .nameProduct(savedProduct.getNameProduct())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .category(category) // Gán đối tượng CategoryResponse
                .imageUrl(savedProduct.getImageUrl())
                .quantity(savedProduct.getQuantity())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())
                .productStatus(savedProduct.getProductStatus())
                .build();
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (request.getNameProduct() != null) {
            existingProduct.setNameProduct(request.getNameProduct());
        }
        if (request.getDescription() != null) {
            existingProduct.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existingProduct.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existingProduct.setCategory(category);
        }
        if (request.getImageUrl() != null) {
            existingProduct.setImageUrl(request.getImageUrl());
        }
        if (request.getQuantity() >= 0) {
            existingProduct.setQuantity(request.getQuantity());
        }
        existingProduct.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(existingProduct);

        // Lấy đối tượng Categories đã được cập nhật
        Categories updatedCategoryEntity = updatedProduct.getCategory();
        CategoryResponse category = null;
        if (updatedCategoryEntity != null) {
            category = CategoryResponse.builder()
                    .id(updatedCategoryEntity.getCategoryId())
                    .name(updatedCategoryEntity.getCategoryName())
                    .build();
        }

        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .nameProduct(updatedProduct.getNameProduct())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .category(category) // Sử dụng categoryResponse đã ánh xạ
                .imageUrl(updatedProduct.getImageUrl())
                .quantity(updatedProduct.getQuantity())
                .createdAt(updatedProduct.getCreatedAt())
                .updatedAt(updatedProduct.getUpdatedAt())
                .productStatus(updatedProduct.getProductStatus())
                .build();
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CategoryResponse categoryResponse = null;
        if (product.getCategory() != null) {
            categoryResponse = CategoryResponse.builder()
                    .id(product.getCategory().getCategoryId())
                    .name(product.getCategory().getCategoryName())
                    .build();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .nameProduct(product.getNameProduct())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryResponse) // Sử dụng categoryResponse đã ánh xạ
                .imageUrl(product.getImageUrl())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .productStatus(product.getProductStatus())
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> {
                    CategoryResponse categoryResponse = null;
                    if (product.getCategory() != null) {
                        categoryResponse = CategoryResponse.builder()
                                .id(product.getCategory().getCategoryId())
                                .name(product.getCategory().getCategoryName())
                                .build();
                    }
                    return ProductResponse.builder()
                            .id(product.getId())
                            .nameProduct(product.getNameProduct())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .category(categoryResponse) // Sử dụng categoryResponse đã ánh xạ
                            .imageUrl(product.getImageUrl())
                            .quantity(product.getQuantity())
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .productStatus(product.getProductStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Lấy danh sách sản phẩm theo trạng thái Activate
    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByProductStatus(ProductStatus.ACTIVATE).stream()
                .map(product -> {
                    CategoryResponse categoryResponse = null;
                    if (product.getCategory() != null) {
                        categoryResponse = CategoryResponse.builder()
                                .id(product.getCategory().getCategoryId())
                                .name(product.getCategory().getCategoryName())
                                .build();
                    }
                    return ProductResponse.builder()
                            .id(product.getId())
                            .nameProduct(product.getNameProduct())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .category(categoryResponse) // Sử dụng categoryResponseelah ánh xạ
                            .imageUrl(product.getImageUrl())
                            .quantity(product.getQuantity())
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt())
                            .productStatus(product.getProductStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        // Check xem sản phẩm có được đặt chưa mới tiến hành xóa
        if (orderItemRepository.existsByProduct_Id(id)) {
            throw new AppException(ErrorCode.PRODUCT_CANNOT_BE_DELETED_DUE_TO_ORDERS);
        }
        productRepository.deleteById(id);
    }

    @Override
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setProductStatus(ProductStatus.ACTIVATE);
        productRepository.save(product);
    }

    @Override
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setProductStatus(ProductStatus.INACTIVATE);
        productRepository.save(product);
    }

    @Override
    public void saleProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setProductStatus(ProductStatus.SALES);
        productRepository.save(product);
    }

    @Override
    public void saleOfProduct(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getQuantity() <= 0) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        if (quantity > product.getQuantity()) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    // thống kê số lượng sản phẩm
    @Override
    public long countProduct() {
        return productRepository.count();
    }

    // Xuất Excel
    @Override
    public byte[] exportProductListToExcel(List<ProductResponse> products) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh sách sản phẩm");

        // Style cho header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorder(headerStyle);

        // Style cho data
        CellStyle dataStyle = workbook.createCellStyle();
        setBorder(dataStyle);

        // Tạo header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Tên sản phẩm", "Mô tả", "Giá", "Số lượng", "Danh mục", "Trạng thái", "Ngày tạo", "Ngày cập nhật"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Đổ dữ liệu sản phẩm vào các row
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (ProductResponse product : products) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, product.getId(), dataStyle);
            createCell(row, 1, product.getNameProduct(), dataStyle);
            createCell(row, 2, product.getDescription() != null ? product.getDescription() : "Trống", dataStyle);
            createCell(row, 3, product.getPrice() != null ? product.getPrice().toString() : "0.0", dataStyle);
            createCell(row, 4, product.getQuantity(), dataStyle);
            createCell(row, 5, product.getCategory() != null ? product.getCategory().getName() : "Trống", dataStyle);
            createCell(row, 6, product.getProductStatus() != null ? product.getProductStatus().name() : "", dataStyle);
            createCell(row, 7, product.getCreatedAt() != null ? product.getCreatedAt().format(formatter) : "", dataStyle);
            createCell(row, 8, product.getUpdatedAt() != null ? product.getUpdatedAt().format(formatter) : "", dataStyle);
        }

        // Auto size các cột cho vừa nội dung
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Thêm bộ lọc
        sheet.setAutoFilter(new CellRangeAddress(0, products.size(), 0, headers.length - 1));

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
