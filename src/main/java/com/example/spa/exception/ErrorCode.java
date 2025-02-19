package com.example.spa.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1009, "Token invalid verify", HttpStatus.UNAUTHORIZED),
    ROLE_INVALID(1010, "Role must be candidate or company", HttpStatus.NOT_FOUND),
    POSITION_INVALID(1011, "Position not found", HttpStatus.NOT_FOUND),
    POSITION_NOT_EXISTED(1012, "Position not found", HttpStatus.NOT_FOUND),
    POSITION_UPDATED(1013, "Position updated successfully", HttpStatus.OK),
    POSITION_CREATED(1014, "Position created successfully", HttpStatus.CREATED),
    POSITION_DELETED(1015, "Position deleted successfully", HttpStatus.NO_CONTENT),
    POSITION_ALREADY_EXISTED(1016, "Position name already existed", HttpStatus.BAD_REQUEST),;
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}