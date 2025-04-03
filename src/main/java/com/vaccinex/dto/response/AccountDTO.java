package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
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
