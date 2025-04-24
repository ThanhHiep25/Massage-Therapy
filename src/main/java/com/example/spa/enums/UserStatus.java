package com.example.spa.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
  ACTIVATE,        // Đang hoạt động
  DEACTIVATED,   // Ngưng hoạt động
  DELETED,
  BLOCKED,       // Khóa tài khoản người dùng
}
