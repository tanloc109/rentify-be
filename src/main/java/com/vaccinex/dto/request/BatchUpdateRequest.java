package com.vaccinex.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BatchUpdateRequest(
        String batchCode,
        Integer vaccineId,
        Integer quantity,
        Integer batchSize,
        LocalDateTime imported,
        LocalDateTime manufactured,
        LocalDateTime expiration,
        String distributer
) {
}
