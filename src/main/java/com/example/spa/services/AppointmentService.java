package com.example.spa.services;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.entities.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(AppointmentRequest request);
}
