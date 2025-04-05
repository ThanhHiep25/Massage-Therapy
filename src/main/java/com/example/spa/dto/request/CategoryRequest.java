package com.example.spa.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryRequest {

    private String categoryName;

    @Override
    public String toString() {
        return "CategoryRequest{" +
                ", name='" +categoryName + '\'' +
                '}';
    }
}
