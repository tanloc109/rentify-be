package com.vaccinex.dto.request;

import com.vaccinex.dto.response.VaccineIntervalResponseDTO;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineUpdateRequest {

    @Size(max = 255, min = 1, message = "Tên phải có từ 1 đến 255 ký tự")
    String name;

    @Size(max = 255, min = 1, message = "Nhà sản xuất phải có từ 1 đến 255 ký tự")
    String manufacturer;

    @Min(value = 1, message = "Giá phải là số dương lớn hơn 1")
    Double price;

    Boolean activated;

    @Size(max = 255, min = 1, message = "Mô tả phải có từ 1 đến 255 ký tự")
    String description;

    @Min(value = 1, message = "Số ngày hết hạn phải là số dương lớn hơn 1")
    Long expiresInDays;

    @Min(value = 1, message = "Độ tuổi tối thiểu phải là số dương lớn hơn 1")
    Integer minAge;

    @Min(value = 1, message = "Độ tuổi tối đa phải là số dương lớn hơn 1")
    Integer maxAge;

    @Min(value = 1, message = "Số liều phải là số dương")
    Integer dose;

    List<VaccineUseResponseDTO> uses;

    List<VaccineTimingCreateRequest> vaccineTimings;

    List<VaccineIntervalResponseDTO> toVaccineIntervals;

}
