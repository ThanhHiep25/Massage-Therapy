package com.example.spa.dto.request;

import com.example.spa.entities.Categories;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private String nameProduct;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String imageUrl;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
