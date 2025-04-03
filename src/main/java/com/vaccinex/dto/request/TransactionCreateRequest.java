package com.vaccinex.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TransactionCreateRequest(
        Integer quantityTaken,
        LocalDateTime date,
        Integer doctorId,
        Integer vaccineId
) {
}
