package com.rentify.auth.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDTO {
    String email;
    String fullName;
    String phoneNumber;
    String password;
    String confirmPassword;
    String role;
}
