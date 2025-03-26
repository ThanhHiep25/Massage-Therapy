package com.example.spa.repositories;

import com.example.spa.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
    boolean existsByCategoryName(String categoryName);

    Categories findByCategoryName(String categoryName);
    // Add custom methods here if needed.

}
