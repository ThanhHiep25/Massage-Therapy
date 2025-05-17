package com.example.spa.services;

import com.example.spa.dto.request.ProductRequest;
import com.example.spa.dto.response.ProductResponse;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productResponse);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getAllProducts();

    // Lấy danh sách sản phẩm theo trạng thái Activate
    List<ProductResponse> getActiveProducts();

    void deleteProduct(Long id);

    void activateProduct(Long id);

    void deactivateProduct(Long id);

    void saleProduct(Long id);

    void saleOfProduct(Long id, int quantity);

    // thống kê số lượng sản phẩm
    long countProduct();

    // Xuất Excel
    byte[] exportProductListToExcel(List<ProductResponse> products) throws IOException;
}
