package com.example.spa.services;

import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.entities.Payment;

public interface MailService {
    void sendMailSCHEDULED(String toEmail, AppointmentResponse appointment);

    void sendMailPAIDPAYM(String email, Payment payment);

    void CANCELAPPOINTMENT(String email);
}
