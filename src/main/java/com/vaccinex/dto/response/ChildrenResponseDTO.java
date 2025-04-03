package com.vaccinex.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChildrenResponseDTO {
    Integer id;
    String firstName;
    String lastName;
    LocalDate dob;
    String gender;
    Double weight;
    Double height;
    String bloodType;
    String healthNote;
    List<InjectionHistoryResponse> injectionHistories;
}
