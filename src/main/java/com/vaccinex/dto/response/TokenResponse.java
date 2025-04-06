package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Value
public class TokenResponse {
    String code;
    String message;
    String token;
    String refreshToken;
    Integer userId;
    String firstName;
    String lastName;
}
