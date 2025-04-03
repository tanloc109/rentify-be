package com.vaccinex.dto.request;

import com.sba301.vaccinex.dto.response.VaccineComboResponseDTO;
import com.sba301.vaccinex.dto.response.VaccineResponseDTO;
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
public class VaccineComboUpdateRequest {

    @Size(max = 255, min = 1, message = "Tên phải có từ 1 đến 255 ký tự")
    String name;

    @Size(max = 255, min = 1, message = "Mô tả phải có từ 1 đến 255 ký tự")
    String description;

    @Min(value = 1, message = "Giá phải là số dương")
    Double price;

    @Min(value = 1, message = "Độ tuổi tối thiểu phải là số dương")
    Integer minAge;

    @Min(value = 1, message = "Độ tuổi tối đa phải là số dương")
    Integer maxAge;

    List<VaccineComboResponseDTO> vaccines;

}
