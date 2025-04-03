package com.vaccinex.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class VaccineInventoryAlert {
    private LocalDate date;
    private int daysFromNow;
    private List<VaccineStockRequirement> vaccineRequirements;

    public boolean hasShortages() {
        return vaccineRequirements.stream().anyMatch(VaccineStockRequirement::isShortage);
    }
}
