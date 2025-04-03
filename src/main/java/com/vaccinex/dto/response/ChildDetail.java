package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChildDetail {
    Integer id;
    String firstName;
    String lastName;
    LocalDate dob;
    String gender;
    Double weight;
    Double height;
    String bloodType;
    String healthNotes;
}
