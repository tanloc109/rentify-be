package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Value
public class VaccineUseResponseDTO {
    Integer id;
    String name;
    String description;
    boolean deleted;
}
