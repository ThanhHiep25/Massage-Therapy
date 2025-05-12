package com.example.spa.dto.response;

import com.example.spa.entities.DiscountCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountCodeResponse {
    private Long discountCodeId;
    private String nameDiscount;
    private String code;
    private int discountAmount;
    private String discountDes;
    private LocalDateTime startDate;
    private LocalDateTime expiredDate;


    public DiscountCodeResponse(DiscountCode discountCode) {
        this.discountCodeId = discountCode.getDiscountCodeId();
        this.nameDiscount = discountCode.getNameDiscount();
        this.code = discountCode.getCode();
        this.discountAmount = discountCode.getDiscountAmount();
        this.discountDes = discountCode.getDiscountDes();
        this.startDate = discountCode.getStartDate();
        this.expiredDate = discountCode.getExpiredDate();
    }
}
