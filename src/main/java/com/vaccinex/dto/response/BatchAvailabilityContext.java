package com.vaccinex.dto.response;

import com.vaccinex.pojo.Batch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAvailabilityContext {
    private int totalAvailable;
    private int requiredVaccines;
    private int requiredVaccinesNext7Days;
    private boolean isAvailable;
    private boolean canBeRescheduled;
    private List<Batch> availableBatches;

    public boolean canBeRescheduled() {
        return this.canBeRescheduled;
    }
}
