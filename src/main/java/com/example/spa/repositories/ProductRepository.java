package com.example.spa.repositories;

import com.example.spa.entities.Product;
import com.example.spa.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameProduct(String nameProduct);

    List<Product> findByProductStatus(ProductStatus productStatus);
}
