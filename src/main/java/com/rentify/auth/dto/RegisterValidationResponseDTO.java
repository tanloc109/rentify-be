package com.rentify.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class RegisterValidationResponseDTO {
    private String message;
    private Map<String, String> fieldErrors;
}
