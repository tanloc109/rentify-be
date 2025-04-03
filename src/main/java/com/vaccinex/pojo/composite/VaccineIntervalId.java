package com.vaccinex.pojo.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class VaccineIntervalId {

    @Column(name = "from_vaccine_id", nullable = false)
    private Integer fromVaccineId;

    @Column(name = "to_vaccine_id", nullable = false)
    private Integer toVaccineId;
}
