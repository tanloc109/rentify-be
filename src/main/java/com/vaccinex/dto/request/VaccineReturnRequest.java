package com.vaccinex.dto.request;

import com.sba301.vaccinex.pojo.enums.Shift;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineReturnRequest {
    Integer doctorId;
    Shift shift;
    List<VaccinesQuantity> returned;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VaccinesQuantity {
        Integer vaccineId;
        Integer quantity;
    }
}
