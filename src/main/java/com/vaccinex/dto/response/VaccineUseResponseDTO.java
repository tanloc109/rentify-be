package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineUseResponseDTO {
    Integer id;
    String name;
    String description;
    boolean deleted;
}
