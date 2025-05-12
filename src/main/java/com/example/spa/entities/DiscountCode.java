package com.example.spa.entities;
import com.example.spa.enums.DiscountCodeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_code")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long discountCodeId;

    @Column(name = "name_discount", nullable = false)
    private String nameDiscount;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "discount_des")
    private String discountDes;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DiscountCodeStatus status ;

    @Override
    public String toString() {
        return "DiscountCode{" +
                "discountCodeId=" + discountCodeId +
                ", nameDiscount='" + nameDiscount + '\'' +
                ", code='" + code + '\'' +
                ", discountDes='" + discountDes + '\'' +
                ", startDate=" + startDate +
                ", expiredDate=" + expiredDate +
                '}';
    }
}
