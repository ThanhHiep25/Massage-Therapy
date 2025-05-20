package com.example.spa.configs;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${url}")  // URL của ứng dụng frontend
    private String frontendUrl;

    @Value("${url_client}")
    private String url_Client;

    @Value("${url_netlify}")
    private String urlNetlify;

    @Value("${url_railway}")
    private String urlRailway;

    @Value("${url_netlify_client}")
    private String url_Net_Client;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Áp dụng cho tất cả các endpoint
                .allowedOrigins(frontendUrl, urlNetlify, urlRailway, url_Client, url_Net_Client) // Cho phép yêu cầu từ ứng dụng frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Các phương thức HTTP được phép
                .allowCredentials(true) // Cho phép gửi cookies nếu cần thiết
                .allowedHeaders("*"); // Cho phép tất cả các headers

    }
}
