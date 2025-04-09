package com.example.spa.services;

import com.example.spa.dto.request.AppointmentRequest;
import com.example.spa.dto.response.AppointmentResponse;
import com.example.spa.entities.Appointment;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);

    Appointment getAppointmentById(Long id);

    List<AppointmentResponse> getAllAppointments();

    Appointment updateAppointment(Long id, AppointmentRequest request);

    void deleteAppointment(Long id);

    List<Appointment> getAllAppointmentsByStatus(String status);

    List<Appointment> getAllAppointmentsByDate(String date);

    void pendingAppointment(Long id);

    void cancelAppointment(Long id);

    void completeAppointment(Long id);

    void scheduledAppointment(Long id);
}
