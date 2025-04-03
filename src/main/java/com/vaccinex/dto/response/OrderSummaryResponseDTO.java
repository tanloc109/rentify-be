package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummaryResponseDTO {
    Integer total;
    Integer paid;
    Integer cancelled;

    private Integer currentMonthTotal;
    private Integer lastMonthTotal;
    private Double changePercentage;
}
