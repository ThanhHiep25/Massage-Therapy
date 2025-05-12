package com.example.spa.services;

import com.example.spa.dto.request.DiscountCodeRequest;
import com.example.spa.dto.response.DiscountCodeResponse;
import com.example.spa.entities.DiscountCode;

import java.util.List;

public interface DiscountCodeService {
    // Rạo khuyén mãi
    DiscountCode createDiscountCode(DiscountCodeRequest discountCodeRequest);

    // Lấy khuyên mái theo id
    DiscountCodeResponse getDiscountCodeById(Long id);

    // Lấy tất cả danh sách khuyên mái
    List<DiscountCodeResponse> getAllDiscountCode();

    // Lấy khuyến mãi theo code
    DiscountCode getDiscountCodeByCode(String code);

    // Xóa khuyên mái
    void deleteDiscountCode(Long id);
}
