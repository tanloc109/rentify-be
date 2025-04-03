package com.vaccinex.base.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElementExistException extends RuntimeException {

    @NotNull
    private String message;

    public ElementExistException(String message) {
        super(message);
        this.message = message;
    }
}