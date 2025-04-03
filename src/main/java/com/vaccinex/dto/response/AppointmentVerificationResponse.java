package com.vaccinex.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentVerificationResponse {
    private Integer vaccineId;
    private String vaccineName;
    private LocalDateTime appointmentDate;
    private boolean isAvailable;
    private boolean canBeRescheduled;
    private int availableQuantity;
    private int requiredQuantity;
    private int requiredQuantityNext7Days;
    private String message;
}