package com.example.spa.dto.response;

import com.example.spa.entities.Categories;
import com.example.spa.entities.Product;
import com.example.spa.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String nameProduct;
    private String description;
    private BigDecimal price;
    private CategoryResponse category;
    private String imageUrl;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductStatus productStatus;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.nameProduct = product.getNameProduct();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.category = new CategoryResponse(product.getCategory());
        this.imageUrl = product.getImageUrl();
        this.quantity = product.getQuantity();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        this.productStatus = product.getProductStatus();
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "id=" + id +
                ", nameProduct='" + nameProduct + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", productStatus=" + productStatus +
                '}';
    }
}
