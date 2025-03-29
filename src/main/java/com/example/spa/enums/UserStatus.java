package com.example.spa.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
  ACTIVATE,        // Đang hoạt động
  DEACTIVATED,   // Bị vô hiệu hóa (có thể khôi phục)
  DELETED,
}
