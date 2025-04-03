package com.vaccinex.dto.response;

import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedOrderResponse {

    String customerName;
    List<Schedule> schedules;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Schedule {
        Integer id;
        LocalDateTime date;
        VaccineScheduleStatus status;
        String doctorName;
        String combo;
        String vaccineName;
    }

    public static BookedOrderResponse fromSchedule(List<VaccineSchedule> schedules) {
        return BookedOrderResponse.builder()
                .customerName(schedules.getFirst().getCustomer().getFullName())
                .schedules(schedules.stream().map(
                        s -> Schedule.builder()
                                .id(s.getId())
                                .date(s.getDate())
                                .status(s.getStatus())
                                .doctorName(s.getDoctor().getFullName())
                                .combo(s.getCombo() != null ? s.getCombo().getName() : null)
                                .vaccineName(s.getVaccine().getName())
                                .build()
                ).toList())
                .build();
    }
}
