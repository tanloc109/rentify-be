package com.vaccinex.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BatchCreateRequest(
        String batchCode,
        Integer vaccineId,
        Integer batchSize,
        LocalDateTime manufactured,
        String distributer
) {}