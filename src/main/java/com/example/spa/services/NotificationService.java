package com.example.spa.services;

import com.example.spa.dto.response.AppointmentResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    void sendAppointmentNotification(AppointmentResponse appointment);

    void sendAppointmentNotificationCustomer();

    SseEmitter subscribe();
}
