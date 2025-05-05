package com.example.spa.dto.response;

import com.example.spa.entities.Categories;
import lombok.*;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private long id;
    private String name;

    public CategoryResponse(Categories category) {
        this.id = category.getCategoryId();
        this.name = category.getCategoryName();
    }
}