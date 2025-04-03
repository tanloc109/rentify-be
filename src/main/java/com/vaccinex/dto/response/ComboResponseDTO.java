package com.vaccinex.dto.response;

import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboResponseDTO {
    Integer id;
    boolean deleted;
    String name;
    String description;
    Double price;
    Integer minAge;
    Integer maxAge;
    List<VaccineComboResponseDTO> vaccines;

    @Transient
    Integer totalQuantity;

    public ComboResponseDTO(Integer id, boolean deleted, String name, String description,
                            Double price, Integer minAge, Integer maxAge,
                            List<VaccineComboResponseDTO> vaccines) {
        this.id = id;
        this.deleted = deleted;
        this.name = name;
        this.description = description;
        this.price = price;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.vaccines = vaccines;
//        this.totalQuantity = vaccines.stream()
//                .filter(Objects::nonNull)
//                .map(VaccineComboResponseDTO::getVaccine)
//                .filter(Objects::nonNull)
//                .map(VaccineResponseDTO::getDose)
//                .filter(Objects::nonNull)
//                .mapToInt(Integer::intValue)
//                .sum();
        this.totalQuantity = (int) vaccines.stream()
                .filter(Objects::nonNull)
                .map(VaccineComboResponseDTO::getVaccine)
                .filter(Objects::nonNull)
                .count();
    }


}
