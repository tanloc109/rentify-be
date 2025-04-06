package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountDTO {
    int id;
    String phone;
    String firstName;
    String lastName;
    String email;
    String roleName;
    boolean enabled;
    boolean nonLocked;
    boolean deleted;
}
