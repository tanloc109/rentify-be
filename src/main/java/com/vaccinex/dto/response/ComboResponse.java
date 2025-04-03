package com.vaccinex.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.vaccinex.pojo.Combo;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonFilter("dynamicFilter")
public class ComboResponse {
    Integer id;
    String name;
    String description;
    Double price;
    Integer minAge;
    Integer maxAge;
    List<Vaccine> vaccines;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonFilter("dynamicFilter")
    public static class Vaccine {
        Integer vaccineId;
        String vaccineName;
        Integer orderInCombo;
    }

    public static ComboResponse fromEntity(Combo combo) {
        return ComboResponse.builder()
                .id(combo.getId())
                .name(combo.getName())
                .description(combo.getDescription())
                .price(combo.getPrice())
                .minAge(combo.getMinAge())
                .maxAge(combo.getMaxAge())
                .vaccines(combo.getVaccineCombos().stream().map(v ->
                        Vaccine.builder()
                                .vaccineId(v.getId().getVaccineId())
                                .vaccineName(v.getVaccine().getName())
                                .orderInCombo(v.getId().getOrderInCombo())
                                .build()
                ).toList())
                .build();
    }
}
