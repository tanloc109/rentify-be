package com.vaccinex.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineUseCreateRequest {
    @NotNull(message = "Vui lòng nhập tên")
    @NotBlank(message = "Tên không được để trống")
    @Size(max = 255, min = 1, message = "Tên phải có độ dài từ 1 đến 255 ký tự")
    String name;

    @NotNull(message = "Vui lòng nhập mô tả")
    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 1000, min = 1, message = "Mô tả phải có độ dài từ 1 đến 1000 ký tự")
    String description;
}
