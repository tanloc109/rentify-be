package com.vaccinex.base.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationException extends RuntimeException {

    @NotNull
    private String message;

    public AuthenticationException(String message) {
        super(message);
        this.message = message;
    }
}