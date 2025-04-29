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
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1004, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1006, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1009, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    NAME_ALREADY_EXISTED(1010, "Name already existed", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTED(1011, "Email already existed", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1012, "Email not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(1013, "Invalid username or password!", HttpStatus.BAD_REQUEST),
    ADD_USER_VALID(200, "Add user successfully", HttpStatus.CREATED),
    USER_ACTIVATED(1111, "Activated", HttpStatus.MULTI_STATUS),
    USER_DELETED(1111, "Deleted", HttpStatus.MULTI_STATUS),
    USER_BLOCKED(1111, "Blocked", HttpStatus.MULTI_STATUS),
    
    //Token
    TOKEN_INVALID(1111, "Token invalid verify", HttpStatus.UNAUTHORIZED),
    //Role
    ROLE_INVALID(1004, "Role must be candidate or company", HttpStatus.NOT_FOUND),
    //Position
    POSITION_INVALID(1004, "Position  error", HttpStatus.NOT_FOUND),
    POSITION_NOT_EXISTED(1006, "Position not found", HttpStatus.NOT_FOUND),
    POSITION_UPDATED(1000, "Position updated successfully", HttpStatus.OK),
    POSITION_CREATED(1000, "Position created successfully", HttpStatus.CREATED),
    POSITION_DELETED(1000, "Position deleted successfully", HttpStatus.NO_CONTENT),
    POSITION_ALREADY_EXISTED(1010, "Position name already existed", HttpStatus.BAD_REQUEST),

    //Staff
    STAFF_INVALID(1001, "Staff must be at least {min}", HttpStatus.NOT_FOUND),
    STAFF_NOT_EXISTED(404, "Staff not found", HttpStatus.NOT_FOUND),
    STAFF_UPDATED(1000, "Staff updated successfully", HttpStatus.OK),
    STAFF_CREATED(1000, "Staff created successfully", HttpStatus.CREATED),
    STAFF_DELETED(1000, "Staff deleted successfully", HttpStatus.NO_CONTENT),
    STAFF_ALREADY_EXISTED(1006, "Staff name already existed", HttpStatus.BAD_REQUEST),
    STAFF_HAS_UNCOMPLETED_APPOINTMENTS(2001,"Staff has uncompleted appointments" ,HttpStatus.BAD_REQUEST ),

    // Department
    DEPARTMENT_INVALID(1001, "Department  error", HttpStatus.NOT_FOUND),
    DEPARTMENT_NOT_FOUND(404, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_UPDATED(1000, "Department updated successfully", HttpStatus.OK),
    DEPARTMENT_CREATED(1000, "Department created successfully", HttpStatus.CREATED),
    DEPARTMENT_DELETED(1000, "Department deleted successfully", HttpStatus.NO_CONTENT),
    DEPARTMENT_ALREADY_EXISTED(1006, "Department name already existed", HttpStatus.BAD_REQUEST),
    MAX_STAFF_PER_ROOM_REACHED(2000 , "Department has max staff", HttpStatus.BAD_REQUEST ),

    // Category
    CATEGORY_INVALID(1001, "Category  error", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1006, "Category not existed", HttpStatus.NOT_FOUND),
    CATEGORY_UPDATED(1000, "Category updated successfully", HttpStatus.OK),
    CATEGORY_CREATED(1000, "Category created successfully", HttpStatus.CREATED),
    CATEGORY_DELETED(1000, "Category deleted successfully", HttpStatus.NO_CONTENT),
    CATEGORY_ALREADY_EXISTED(1000, "Category name already existed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND (404, "Category not found", HttpStatus.NOT_FOUND),

    // Candidate
    CANDIDATE_INVALID(1001, "Candidate  error", HttpStatus.NOT_FOUND),
    CANDIDATE_NOT_FOUND(404, "Candidate not found", HttpStatus.NOT_FOUND),
    CANDIDATE_UPDATED(1000, "Candidate updated successfully", HttpStatus.OK),
    CANDIDATE_CREATED(1000, "Candidate created successfully", HttpStatus.CREATED),
    CANDIDATE_DELETED(1000, "Candidate deleted successfully", HttpStatus.NO_CONTENT),
    CANDIDATE_ALREADY_EXISTED(1006, "Candidate name already existed", HttpStatus.BAD_REQUEST),

    // Payment
    PAYMENT_INVALID(1001, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_NOT_EXISTED(404, "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_UPDATED(1000, "Payment updated successfully", HttpStatus.OK),
    PAYMENT_CREATED(1000, "Payment created successfully", HttpStatus.CREATED),
    PAYMENT_DELETED(1000, "Payment deleted successfully", HttpStatus.NO_CONTENT),
    PAYMENT_ALREADY_EXISTED(1006, "Payment name already existed", HttpStatus.BAD_REQUEST),

    //  Appointment
    APPOINTMENT_INVALID(1001, "Appointment  error", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_FOUND(404, "Appointment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_UPDATED(1000, "Appointment updated successfully", HttpStatus.OK),
    APPOINTMENT_CREATED(1000, "Appointment created successfully", HttpStatus.CREATED),
    APPOINTMENT_DELETED(1000, "Appointment deleted successfully", HttpStatus.NO_CONTENT),
    APPOINTMENT_ALREADY_EXISTED(1000, "Appointment name already existed", HttpStatus.BAD_REQUEST),
    APPOINTMENT_ERROR(1001, "Appointment error", HttpStatus.BAD_REQUEST),
    APPOINTMENT_PENDING(1111, "Appointment pending", HttpStatus.OK),
    APPOINTMENT_CANCELLED(1222, "Appointment cancelled", HttpStatus.OK),
    APPOINTMENT_COMPLETED(1333, "Appointment completed", HttpStatus.OK),
    APPOINTMENT_SCHEDULED(1444, "Appointment scheduled", HttpStatus.OK),

    // Service
    SERVICE_INVALID(1001, "Service  error", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(404, "Service not found", HttpStatus.NOT_FOUND),
    SERVICE_UPDATED(1000, "Service updated successfully", HttpStatus.OK),
    SERVICE_CREATED(1000, "Service created successfully", HttpStatus.OK),
    SERVICE_DELETED(1000, "Service deleted successfully", HttpStatus.NO_CONTENT),
    SERVICE_ALREADY_EXISTED(1006, "Service name already existed", HttpStatus.BAD_REQUEST),
    SERVICE_ERROR(405, "Service error", HttpStatus.BAD_REQUEST),

    // Service Staff
    STAFF_SERVICE_NOT_FOUND(404, "Service not found", HttpStatus.NOT_FOUND),
    STAFF_SERVICE_INVALID(1001, "Service  error", HttpStatus.NOT_FOUND),
    STAFF_SERVICE_UPDATED(1000, "Service updated successfully", HttpStatus.OK),
    STAFF_SERVICE_CREATED(1000, "Service created successfully", HttpStatus.CREATED),
    STAFF_SERVICE_DELETED(1000, "Service deleted successfully", HttpStatus.NO_CONTENT),
    STAFF_SERVICE_ALREADY_EXISTED(1006, "Service name already existed", HttpStatus.BAD_REQUEST),

    // Product
    PRODUCT_INVALID(1001, "Product  error", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(404, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_UPDATED(1000, "Product updated successfully", HttpStatus.OK),
    PRODUCT_CREATED(1000, "Product created successfully", HttpStatus.CREATED),
    PRODUCT_DELETED(1000, "Product deleted successfully", HttpStatus.NO_CONTENT),
    PRODUCT_ALREADY_EXISTED(1006, "Product name already existed", HttpStatus.BAD_REQUEST),
    PRODUCT_OUT_OF_STOCK(1010, "Product out of stock", HttpStatus.BAD_REQUEST),


    ORDER_NOT_FOUND(404,"Order not found" ,HttpStatus.NOT_FOUND ),
    INVALID_ORDER_STATUS(1010, "Invalid order status", HttpStatus.BAD_REQUEST),


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