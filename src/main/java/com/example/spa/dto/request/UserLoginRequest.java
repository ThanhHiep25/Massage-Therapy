package com.example.spa.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginRequest {
    private String email;
    private String password;
}
