package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineReportResponseDTO {
    Integer id;
    String name;
    String vaccineCode;
    String manufacturer;
    Integer quantity;
}
