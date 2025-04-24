package com.example.spa.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {
    private String email;
    private String otp;
}