package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineDetail {
    Integer id;
    String name;
    String vaccineCode;
    String description;
}
