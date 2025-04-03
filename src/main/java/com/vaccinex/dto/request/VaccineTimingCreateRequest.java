package com.vaccinex.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTimingCreateRequest {

    @NotNull(message = "Vui lòng nhập số liều")
    @Positive(message = "Số liều phải là số dương")
    Integer doseNo;

    @NotNull(message = "Vui lòng nhập số ngày sau liều trước")
    @Positive(message = "Số ngày sau liều trước phải là số dương")
    Long daysAfterPreviousDose;
}
