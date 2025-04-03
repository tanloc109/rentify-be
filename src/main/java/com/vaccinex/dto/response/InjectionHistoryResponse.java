package com.vaccinex.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InjectionHistoryResponse {
    Integer id;
    LocalDateTime dateTime;
    String vaccine;
    String status;
}
