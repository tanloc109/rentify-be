package com.vaccinex.dto.response;

import lombok.Builder;

@Builder
public record VaccineDTO(
        Integer id,
        String vaccineCode,
        String name
) {
}

