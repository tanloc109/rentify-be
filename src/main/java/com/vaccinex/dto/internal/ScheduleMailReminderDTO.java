package com.vaccinex.dto.internal;

import com.vaccinex.pojo.VaccineSchedule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleMailReminderDTO {
    String userEmail;
    String userFullName;
    String childFullName;
    LocalDateTime date;
    String notes;
    String vaccineCode;
    String vaccineName;
    String comboName; // Can be null
    String doctorFullName;

    public static ScheduleMailReminderDTO fromEntity(VaccineSchedule schedule) {
        var dto = ScheduleMailReminderDTO.builder()
                .userEmail(schedule.getCustomer().getEmail())
                .userFullName(schedule.getCustomer().getFullName())
                .childFullName(schedule.getChild().getFullName())
                .date(schedule.getDate())
                .notes(schedule.getNotes())
                .vaccineCode(schedule.getVaccine().getVaccineCode())
                .vaccineName(schedule.getVaccine().getName())
                .doctorFullName(schedule.getDoctor().getFullName())
                .build();
        if (schedule.getCombo() != null) {
            dto.setComboName(schedule.getCombo().getName());
        }
        return dto;
    }
}
