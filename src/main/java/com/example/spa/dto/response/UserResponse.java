package com.example.spa.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private String phone;
    private String name;
    private String address;
    private String imageUrl;
    private String role;

}
