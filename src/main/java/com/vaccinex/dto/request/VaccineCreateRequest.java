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
public class VaccineCreateRequest {

    @NotNull(message = "Vui lòng nhập tên")
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 255, min = 1, message = "Tên phải có từ 1 đến 255 ký tự")
    String name;

    @NotNull(message = "Vui lòng nhập mã vaccine")
    @NotBlank(message = "Mã vaccine không được để trống")
    @Size(max = 255, min = 1, message = "Mã vaccine phải có từ 1 đến 255 ký tự")
    String vaccineCode;

    @NotNull(message = "Vui lòng nhập mô tả")
    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 255, min = 1, message = "Mô tả phải có từ 1 đến 255 ký tự")
    String description;

    @NotNull(message = "Vui lòng nhập nhà sản xuất")
    @NotBlank(message = "Nhà sản xuất không được để trống")
    @Size(max = 255, min = 1, message = "Nhà sản xuất phải có từ 1 đến 255 ký tự")
    String manufacturer;

    @NotNull(message = "Vui lòng nhập giá")
    @Positive(message = "Giá phải là số dương")
    double price;

    @NotNull(message = "Vui lòng nhập số ngày hết hạn")
    @Positive(message = "Số ngày hết hạn phải là số dương")
    long expiresInDays;

    @NotNull(message = "Vui lòng nhập độ tuổi tối thiểu")
    @Positive(message = "Độ tuổi tối thiểu phải là số dương")
    Integer minAge;

    @NotNull(message = "Vui lòng nhập độ tuổi tối đa")
    @Positive(message = "Độ tuổi tối đa phải là số dương")
    Integer maxAge;

    @NotNull(message = "Vui lòng nhập số liều")
    @Positive(message = "Số liều phải là số dương")
    Integer dose;

    boolean activated;

    @NotNull(message = "Vui lòng nhập mục đích sử dụng của vaccine")
    @NotEmpty(message = "Mục đích sử dụng của vaccine không được để trống")
    List<VaccineUseResponseDTO> uses;

    List<VaccineTimingCreateRequest> vaccineTimings;

    @NotNull(message = "Vui lòng nhập danh sách ràng buộc vaccine")
    @NotEmpty(message = "Danh sách ràng buộc vaccine không được để trống")
    List<VaccineIntervalResponseDTO> toVaccineIntervals;
}
