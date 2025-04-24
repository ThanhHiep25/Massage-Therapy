package com.example.spa.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private String vnp_ResponseCode;

    private String vnp_TransactionNo;

    private String vnp_Amount;

    private String vnp_BankCode;

    private String vnp_BankTranNo;

    private String data;

    @Override
    public String toString() {
        return "PaymentDTO [vnp_ResponseCode=" + vnp_ResponseCode + ", vnp_TransactionNo=" + vnp_TransactionNo
                + ", vnp_Amount=" + vnp_Amount + ", vnp_BankCode=" + vnp_BankCode + ", vnp_BankTranNo=" + vnp_BankTranNo
                + ", data=" + data + "]";
    }

}
