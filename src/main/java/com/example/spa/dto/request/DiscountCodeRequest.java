package com.example.spa.dto.request;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DiscountCodeRequest {
    private String nameDiscount;
    private String code;
    private int discountAmount;
    private String discountDes;
    private LocalDateTime startDate;
    private LocalDateTime expiredDate;

    @Override
    public String toString() {
        return "DiscountCodeRequest{" +
                "nameDiscount='" + nameDiscount + '\'' +
                ", code='" + code + '\'' +
                ", discountAmount=" + discountAmount +
                ", discountDes='" + discountDes + '\'' +
                ", startDate=" + startDate +
                ", expiredDate=" + expiredDate +
                '}';
    }
}
