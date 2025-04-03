package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineResponseIntervalCustomDTO {
    Integer id;
    boolean deleted;
    String name;
    String vaccineCode;
    boolean activated;
}
