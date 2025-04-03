package com.vaccinex.dto.response;

import com.vaccinex.pojo.Vaccine;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class BatchWithRemaining {
    Integer id;
    String batchCode;
    LocalDateTime imported;
    LocalDateTime expiration;
    Vaccine vaccine;
    int quantity;
}