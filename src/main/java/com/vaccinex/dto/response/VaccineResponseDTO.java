package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineResponseDTO {
    Integer id;
    boolean deleted;
    String name;
    String vaccineCode;
    boolean activated;
    String manufacturer;
    String description;
    Double price;
    Long expiresInDays;
    Integer minAge;
    Integer maxAge;
    Integer dose;
    List<VaccineUseResponseDTO> uses;
    List<VaccineTimingResponseDTO> vaccineTimings;
    List<VaccineIntervalResponseDTO> toVaccineIntervals;

}
