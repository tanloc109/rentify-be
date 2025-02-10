package com.rentify.auth.dto;

import com.rentify.user.dto.UserDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponseDTO {
    private String accessToken;
    private UserDTO user;
}
