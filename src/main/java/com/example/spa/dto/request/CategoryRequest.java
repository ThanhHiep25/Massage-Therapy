package com.example.spa.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    private String name;

    @Override
    public String toString() {
        return "CategoryRequest{" +
                ", name='" + name + '\'' +
                '}';
    }
}
