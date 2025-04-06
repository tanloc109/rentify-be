package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTimingResponseDTO {
    Integer id;
    Integer doseNo;
    Integer daysAfterPreviousDose;
    boolean deleted;

}
