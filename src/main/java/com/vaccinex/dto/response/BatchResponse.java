package com.vaccinex.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vaccinex.pojo.Batch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("dynamicFilter")
public class BatchResponse {
    Integer id;
    String batchCode;
    Integer batchSize;
    Integer quantity;
    LocalDateTime imported;
    LocalDateTime manufactured;
    LocalDateTime expiration;
    String distributer;
    Integer vaccineId;
    String vaccineName;
    String vaccineDescription;
    String vaccineCode;
    String vaccineManufacturer;
    Double vaccinePrice;
    Long vaccineExpiresInDays;
    Integer vaccineMinAge;
    Integer vaccineMaxAge;
    Integer vaccineDose;

    public static BatchResponse fromEntity(Batch b) {
        return BatchResponse.builder()
                .id(b.getId())
                .batchCode(b.getBatchCode())
                .batchSize(b.getBatchSize())
                .quantity(b.getQuantity())
                .imported(b.getImported())
                .expiration(b.getExpiration())
                .manufactured(b.getManufactured())
                .distributer(b.getDistributer())
                .vaccineId(b.getVaccine().getId())
                .vaccineName(b.getVaccine().getName())
                .vaccineDescription(b.getVaccine().getDescription())
                .vaccineCode(b.getVaccine().getVaccineCode())
                .vaccineManufacturer(b.getVaccine().getManufacturer())
                .vaccinePrice(b.getVaccine().getPrice())
                .vaccineExpiresInDays(b.getVaccine().getExpiresInDays())
                .vaccineMinAge(b.getVaccine().getMinAge())
                .vaccineMaxAge(b.getVaccine().getMaxAge())
                .vaccineDose(b.getVaccine().getDose())
                .build();
    }
}