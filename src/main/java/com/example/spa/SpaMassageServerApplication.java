package com.example.spa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.spa")
public class SpaMassageServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaMassageServerApplication.class, args);
    }
}
