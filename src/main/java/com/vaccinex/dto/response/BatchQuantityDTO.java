package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchQuantityDTO {
    Integer vaccineId;
    String vaccineCode;
    Integer expiredQuantity;
    Integer totalQuantity;
    Integer scheduledQuantity;
    Integer quantityAboutToBeExpired;
    LocalDateTime dateAboutToBeExpired;
    LocalDateTime latestExpiresIn;
}
