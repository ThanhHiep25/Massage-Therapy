package com.example.spa.enums;

public enum StaffServiceStatus {
    Unassigned, // Chưa phân công
    Assigning, // Đang phân công
    Assigned, // Đã phân công
    InProgress, // Đang thực hiện
    Completed, // Hoàn thành
    Cancelled, // Hủy phân công
    Approval, // Chờ phê duyệt
    Overdue, // Không hoàn thành (Quá thời hạn)
}
