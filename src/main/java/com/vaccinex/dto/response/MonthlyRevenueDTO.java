package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyRevenueDTO {
    private String month;
    private Double value;
    private Double grossRevenue;
    private Double refund;
}
