package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueResponseDTO {
    Double grossRevenue;
    Double netRevenue;
    Double totalRefund;
    Integer totalOrders;

    Double currentMonthRevenue;
    Double previousMonthRevenue;
    Double revenueChangePercentage;
    Integer currentMonthOrders;
    Integer previousMonthOrders;
    Double ordersChangePercentage;

    List<MonthlyRevenueDTO> monthlyData;
}
