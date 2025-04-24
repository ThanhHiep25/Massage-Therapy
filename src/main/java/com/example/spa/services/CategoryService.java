package com.example.spa.services;


import com.example.spa.dto.request.CategoryRequest;
import com.example.spa.entities.Categories;

public interface CategoryService {
    Categories createCategory(CategoryRequest category);

    Categories getCategoryById(Long id);

    Categories updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    Iterable<Categories> getAllCategories();

    boolean existsByCategoryName(String categoryName);

    Categories findByCategoryName(String categoryName);

    void deleteAllCategories();

    // Thống kê tổng doanh mục
    long countCategories();
}
