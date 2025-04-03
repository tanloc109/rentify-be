package com.vaccinex.base.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnchangedStateException extends RuntimeException {

    @NotNull
    private String message;

    public UnchangedStateException(String message) {
        super(message);
        this.message = message;
    }
}