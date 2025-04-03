package com.vaccinex.dto.response;

import jakarta.json.bind.annotation.JsonbDateFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
    String status;
    String message;
    Object error;
    String path;
}
