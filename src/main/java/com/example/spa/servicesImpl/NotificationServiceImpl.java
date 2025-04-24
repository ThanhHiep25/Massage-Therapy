package com.example.spa.servicesImpl;

import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.services.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @Override
    public void sendAppointmentNotification(AppointmentResponse appointment) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("appointment-admin")
                        .data(appointment, MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    @Override
    public void sendAppointmentNotificationCustomer() {
        String customerNotification = "Lịch hẹn của bạn đang chờ xác nhận. Hãy đợi thông tin về email của bạn.";
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("appointment-customer")
                        .data(customerNotification, MediaType.TEXT_PLAIN)); // Gửi dưới dạng text
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    @Override
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }
}