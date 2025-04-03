package com.vaccinex.service;

import com.vaccinex.dto.response.AppointmentVerificationResponse;

import java.time.LocalDateTime;

public interface AppointmentVerificationService {
    AppointmentVerificationResponse verifyAppointmentAvailability(Integer vaccineId, LocalDateTime appointmentDate);
}