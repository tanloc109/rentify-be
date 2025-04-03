package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTimingResponseDTO {
    Integer id;
    Integer doseNo;
    Integer daysAfterPreviousDose;
    boolean deleted;

}
