package com.example.spa.servicesImpl;

import com.example.spa.dto.request.DiscountCodeRequest;
import com.example.spa.dto.response.DiscountCodeResponse;
import com.example.spa.entities.DiscountCode;
import com.example.spa.enums.DiscountCodeStatus;
import com.example.spa.exception.AppException;
import com.example.spa.exception.ErrorCode;
import com.example.spa.repositories.DiscountCodeRepository;
import com.example.spa.services.DiscountCodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountCodeServiceImpl implements DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;

    // Tạo khuyén mãi
    @Override
    @Transactional
    public DiscountCode createDiscountCode(DiscountCodeRequest discountCodeRequest) {
      // Check code trùng
        if (discountCodeRepository.findByCode(discountCodeRequest.getCode()) != null) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_EXISTED);
        }

        DiscountCode discountCode = new DiscountCode();
        discountCode.setNameDiscount(discountCodeRequest.getNameDiscount());
        discountCode.setCode(discountCodeRequest.getCode());
        discountCode.setDiscountAmount(discountCodeRequest.getDiscountAmount());
        discountCode.setDiscountDes(discountCodeRequest.getDiscountDes());
        discountCode.setStartDate(discountCodeRequest.getStartDate());
        discountCode.setExpiredDate(discountCodeRequest.getExpiredDate());
        discountCode.setStatus(DiscountCodeStatus.ACTIVATE);
        return discountCodeRepository.save(discountCode);
    }

    // Lấy khuyến mãi theo id
    @Override
    public DiscountCodeResponse getDiscountCodeById(Long id) {
        DiscountCode discountCode = discountCodeRepository.findById(id).orElseThrow(() -> new RuntimeException("DiscountCode not found"));
        return new DiscountCodeResponse(discountCode);
    }

    // Lấy tất cả danh sách khuyên mãi
    @Override
    public List<DiscountCodeResponse> getAllDiscountCode() {
        return discountCodeRepository.findAll().stream()
                .map(DiscountCodeResponse::new)
                .toList();
    }


    // Lấy khuyến mãi theo code
    @Override
    public DiscountCode getDiscountCodeByCode(String code) {
        return discountCodeRepository.findByCode(code);
    }

    // Xóa khuyên mái
    @Override
    public void deleteDiscountCode(Long id) {
        discountCodeRepository.deleteById(id);
    }


}
