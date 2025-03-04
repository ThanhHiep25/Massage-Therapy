package com.example.spa.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //Common
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    //User
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    NAME_ALREADY_EXISTED(1009, "Name already existed", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTED(1010, "Email already existed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1011, "Email not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1011, "Invalid username or password!", HttpStatus.BAD_REQUEST),
    //Token
    TOKEN_INVALID(1009, "Token invalid verify", HttpStatus.UNAUTHORIZED),
    //Role
    ROLE_INVALID(1010, "Role must be candidate or company", HttpStatus.NOT_FOUND),
    //Position
    POSITION_INVALID(1011, "Position not found", HttpStatus.NOT_FOUND),
    POSITION_NOT_EXISTED(1012, "Position not found", HttpStatus.NOT_FOUND),
    POSITION_UPDATED(1013, "Position updated successfully", HttpStatus.OK),
    POSITION_CREATED(1014, "Position created successfully", HttpStatus.CREATED),
    POSITION_DELETED(1015, "Position deleted successfully", HttpStatus.NO_CONTENT),
    POSITION_ALREADY_EXISTED(1016, "Position name already existed", HttpStatus.BAD_REQUEST),

    //Staff
    STAFF_INVALID(1017, "Staff must be at least {min}", HttpStatus.NOT_FOUND),
    STAFF_NOT_EXISTED(1018, "Staff not found", HttpStatus.NOT_FOUND),
    STAFF_UPDATED(1019, "Staff updated successfully", HttpStatus.OK),
    STAFF_CREATED(1020, "Staff created successfully", HttpStatus.CREATED),
    STAFF_DELETED(1021, "Staff deleted successfully", HttpStatus.NO_CONTENT),
    STAFF_ALREADY_EXISTED(1022, "Staff name already existed", HttpStatus.BAD_REQUEST),

    // Department
    DEPARTMENT_INVALID(1023, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_NOT_EXISTED(1024, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_UPDATED(1025, "Department updated successfully", HttpStatus.OK),
    DEPARTMENT_CREATED(1026, "Department created successfully", HttpStatus.CREATED),
    DEPARTMENT_DELETED(1027, "Department deleted successfully", HttpStatus.NO_CONTENT),
    DEPARTMENT_ALREADY_EXISTED(1028, "Department name already existed", HttpStatus.BAD_REQUEST),

    // Category
    CATEGORY_INVALID(1029, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1030, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_UPDATED(1031, "Category updated successfully", HttpStatus.OK),
    CATEGORY_CREATED(1032, "Category created successfully", HttpStatus.CREATED),
    CATEGORY_DELETED(1033, "Category deleted successfully", HttpStatus.NO_CONTENT),
    CATEGORY_ALREADY_EXISTED(1034, "Category name already existed", HttpStatus.BAD_REQUEST),

    // Candidate
    CANDIDATE_INVALID(1035, "Candidate not found", HttpStatus.NOT_FOUND),
    CANDIDATE_NOT_EXISTED(1036, "Candidate not found", HttpStatus.NOT_FOUND),
    CANDIDATE_UPDATED(1037, "Candidate updated successfully", HttpStatus.OK),
    CANDIDATE_CREATED(1038, "Candidate created successfully", HttpStatus.CREATED),
    CANDIDATE_DELETED(1039, "Candidate deleted successfully", HttpStatus.NO_CONTENT),
    CANDIDATE_ALREADY_EXISTED(1040, "Candidate name already existed", HttpStatus.BAD_REQUEST),

    // Payment
    PAYMENT_INVALID(1041, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(1042, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_UPDATED(1043, "Payment updated successfully", HttpStatus.OK),
    PAYMENT_CREATED(1044, "Payment created successfully", HttpStatus.CREATED),
    PAYMENT_DELETED(1045, "Payment deleted successfully", HttpStatus.NO_CONTENT),
    PAYMENT_ALREADY_EXISTED(1046, "Payment name already existed", HttpStatus.BAD_REQUEST),
    ;
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}