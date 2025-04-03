package com.vaccinex.dto.response;

import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleDetail {
    Integer id;
    Integer feedback;
    ChildDetail child;
    OrderDetail order;
    LocalDateTime date;
    String doctorName;
    VaccineDetail vaccine;
    List<PastSchedule> pastSchedules;
    List<ReactionDetail> reactions;
    VaccineScheduleStatus status;
}

