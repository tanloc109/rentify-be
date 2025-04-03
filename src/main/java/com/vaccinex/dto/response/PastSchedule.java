package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PastSchedule {
    Integer id;
    LocalDateTime date;
    String vaccineName;
    String doctorId;
    Integer feedback;
    String comboName;
    List<ReactionDetail> reactions;
}
