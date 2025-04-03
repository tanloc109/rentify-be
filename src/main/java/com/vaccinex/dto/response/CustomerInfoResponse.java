package com.vaccinex.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CustomerInfoResponse {
    Integer id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    LocalDate dob;
}
