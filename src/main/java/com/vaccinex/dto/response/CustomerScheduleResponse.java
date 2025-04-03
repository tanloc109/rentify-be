package com.vaccinex.dto.response;

import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerScheduleResponse {
    Integer id;
    String vaccineName;
    LocalDateTime date;
    String doctorName;
    String childName;
    VaccineScheduleStatus status;

    public static CustomerScheduleResponse fromEntity(VaccineSchedule schedule) {
        return CustomerScheduleResponse.builder()
                .id(schedule.getId())
                .vaccineName(schedule.getVaccine().getName())
                .date(schedule.getDate())
                .doctorName(schedule.getDoctor().getFullName())
                .childName(schedule.getChild().getFullName())
                .status(schedule.getStatus())
                .build();
    }
}
