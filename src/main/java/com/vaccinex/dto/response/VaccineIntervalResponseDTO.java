package com.vaccinex.dto.response;

import com.vaccinex.pojo.composite.VaccineIntervalId;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineIntervalResponseDTO {
    VaccineIntervalId id;
    VaccineResponseIntervalCustomDTO toVaccine;
    long daysBetween;
}
