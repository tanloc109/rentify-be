package com.rentify.user.dto;

import com.rentify.user.entity.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String fullName;
    String email;
    String phoneNumber;
    Role role;
    Timestamp version;
}
