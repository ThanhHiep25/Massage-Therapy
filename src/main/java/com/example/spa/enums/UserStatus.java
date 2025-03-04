package com.example.spa.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
  ACTIVE,        // Đang hoạt động
  DEACTIVATED,   // Bị vô hiệu hóa (có thể khôi phục)
  DELETED        // Bị xóa (ẩn khỏi danh sách hiển thị)
}
