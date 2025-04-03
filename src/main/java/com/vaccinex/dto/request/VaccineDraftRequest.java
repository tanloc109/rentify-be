package com.vaccinex.dto.request;

import com.vaccinex.pojo.enums.ServiceType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VaccineDraftRequest(
        List<Integer> ids,
        Integer doctorId,
        Integer childId,
        Integer customerId,
        LocalDateTime desiredDate,
        ServiceType serviceType
) {
}
