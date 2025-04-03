package com.vaccinex.dto.request;

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
public class VaccineUseUpdateRequest {

    @Size(max = 255, min = 1, message = "Tên phải có độ dài từ 1 đến 255 ký tự")
    String name;

    @Size(max = 1000, min = 1, message = "Mô tả phải có độ dài từ 1 đến 1000 ký tự")
    String description;
}
