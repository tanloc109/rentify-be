package com.vaccinex.dto.response;

import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DoctorScheduleResponse {
    Integer id;
    LocalDateTime dateTime;
    String firstName;
    String lastName;
    String vaccine;
    VaccineScheduleStatus status;
}
