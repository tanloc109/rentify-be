package com.vaccinex.dto.request;

import com.vaccinex.dto.response.VaccineComboResponseDTO;
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
public class VaccineComboCreateRequest {

    @NotNull(message = "Vui lòng nhập tên")
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 255, min = 1, message = "Tên phải có từ 1 đến 255 ký tự")
    String name;

    @NotNull(message = "Vui lòng nhập mô tả")
    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 255, min = 1, message = "Mô tả phải có từ 1 đến 255 ký tự")
    String description;

    @NotNull(message = "Vui lòng nhập giá")
    @Positive(message = "Giá phải là số dương")
    Double price;

    @NotNull(message = "Vui lòng nhập độ tuổi tối thiểu")
    @Positive(message = "Độ tuổi tối thiểu phải là số dương")
    Integer minAge;

    @NotNull(message = "Vui lòng nhập độ tuổi tối đa")
    @Positive(message = "Độ tuổi tối đa phải là số dương")
    Integer maxAge;

    @NotNull(message = "Vui lòng nhập danh sách vaccine trong combo")
    @NotEmpty(message = "Danh sách vaccine trong combo không được để trống")
    List<VaccineComboResponseDTO> vaccines;
}
