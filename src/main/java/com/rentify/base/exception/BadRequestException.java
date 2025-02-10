package com.rentify.base.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, String> errors;

    public BadRequestException(String message) {
        super(message);
        this.errors = null;
    }

    public BadRequestException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

}