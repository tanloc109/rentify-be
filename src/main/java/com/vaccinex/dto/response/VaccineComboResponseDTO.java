package com.vaccinex.dto.response;

import com.vaccinex.pojo.composite.VaccineComboId;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineComboResponseDTO {
    VaccineComboId id;

    VaccineResponseDTO vaccine;

    ComboResponseDTO combo;

    Long intervalDays;

}
