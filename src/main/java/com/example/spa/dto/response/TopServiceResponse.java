package com.example.spa.dto.response;


import lombok.*;

@Data
@AllArgsConstructor
public class TopServiceResponse {
    private String serviceName;
    private Long count;
}
