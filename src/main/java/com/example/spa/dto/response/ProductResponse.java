package com.example.spa.dto.response;

import com.example.spa.entities.Categories;
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
    private Categories category;
    private String imageUrl;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductStatus productStatus;

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
