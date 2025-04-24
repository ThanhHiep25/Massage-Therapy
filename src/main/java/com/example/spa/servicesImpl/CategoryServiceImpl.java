package com.example.spa.servicesImpl;

import com.example.spa.dto.request.CategoryRequest;
import com.example.spa.entities.Categories;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.CategoryRepository;
import com.example.spa.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    @Transactional
    public Categories createCategory(CategoryRequest category) {
        // Kiểm tra xem danh mục đã tồn tại chưa (theo tên)
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        // Tạo danh mục mới
        Categories newCategory = new Categories();
        newCategory.setCategoryName(category.getCategoryName());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Categories getCategoryById(Long id) {
       return categoryRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_EXISTED));
    }

    @Override
    @Transactional
    public Categories updateCategory(Long id, CategoryRequest request) {
        try {
            Categories category = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            category.setCategoryName(request.getCategoryName());
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new AppException(ErrorCode.CATEGORY_INVALID);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    @Override
    public Iterable<Categories> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    public Categories findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public void deleteAllCategories() {
       try {
              categoryRepository.deleteAll();
         } catch (Exception e) {
              throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
       }
    }

    // Thống kê tổng doanh mục
    @Override
    public long countCategories() {
        return categoryRepository.count();
    }



}
