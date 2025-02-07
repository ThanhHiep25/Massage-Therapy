package com.example.spa.dto.response;

import com.example.spa.entities.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {
    private String username;
    private String email;
    private String name;
    private String phone;
    private String address;
    private String createdAt;
    private String imageUrl;
    private String role;
}
