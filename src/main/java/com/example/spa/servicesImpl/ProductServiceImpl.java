package com.example.spa.servicesImpl;

import com.example.spa.dto.request.ProductRequest;
import com.example.spa.dto.response.ProductResponse;
import com.example.spa.entities.Categories;
import com.example.spa.entities.Product;
import com.example.spa.enums.ProductStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.CategoryRepository;
import com.example.spa.repositories.ProductRepository;
import com.example.spa.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoriesRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Categories category = categoriesRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .nameProduct(request.getNameProduct())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .imageUrl(request.getImageUrl())
                .quantity(request.getQuantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .productStatus(ProductStatus.ACTIVATE) // Mặc định là ACTIVE khi tạo mới
                .build();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.builder()
                .id(savedProduct.getId())
                .nameProduct(savedProduct.getNameProduct())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .category(savedProduct.getCategory())
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
        if (request.getCategoryId() != null) { // Check for categoryId in the request
            Categories category = categoriesRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            existingProduct.setCategory(category);
        }
        if (request.getImageUrl() != null) {
            existingProduct.setImageUrl(request.getImageUrl());
        }
        if (request.getQuantity() >= 0) { // Cho phép giảm số lượng
            existingProduct.setQuantity(request.getQuantity());
        }
        existingProduct.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductResponse.builder()
                .id(updatedProduct.getId())
                .nameProduct(updatedProduct.getNameProduct())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .category(updatedProduct.getCategory())
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
        return ProductResponse.builder()
                .id(product.getId())
                .nameProduct(product.getNameProduct())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .quantity(product.getQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .nameProduct(product.getNameProduct())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .category(product.getCategory())
                        .imageUrl(product.getImageUrl())
                        .quantity(product.getQuantity())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
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
    public void saleProduct(Long id){
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
}
