package com.vaccinex.base.exception.mapper;

import com.vaccinex.base.exception.AuthenticationException;
import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.dto.response.ErrorResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Provider
@ApplicationScoped
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.log(Level.SEVERE, "Handling exception", exception);

        // Handle specific exception types
        if (exception instanceof ConstraintViolationException constraintViolationEx) {
            return handleConstraintViolationException(constraintViolationEx);
        }

        if (exception instanceof ValidationException validationEx) {
            return createErrorResponse(Response.Status.BAD_REQUEST, 
                "Validation Error", 
                validationEx.getMessage());
        }

        if (exception instanceof EntityNotFoundException entityNotFoundEx) {
            return createErrorResponse(Response.Status.NOT_FOUND, 
                "Entity Not Found", 
                entityNotFoundEx.getMessage());
        }

        if (exception instanceof PersistenceException persistenceEx) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Database Error", 
                "An error occurred while processing database operation");
        }

        if (exception instanceof SQLException sqlEx) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Database Connection Error", 
                "A database connection error occurred");
        }

        if (exception instanceof IOException ioEx) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "I/O Error", 
                "An input/output error occurred");
        }

        if (exception instanceof NotAuthorizedException notAuthEx) {
            return createErrorResponse(Response.Status.UNAUTHORIZED, 
                "Unauthorized Access", 
                notAuthEx.getMessage());
        }

        if (exception instanceof ForbiddenException forbiddenEx) {
            return createErrorResponse(Response.Status.FORBIDDEN, 
                "Access Forbidden", 
                forbiddenEx.getMessage());
        }

        if (exception instanceof BadRequestException badRequestEx) {
            return createErrorResponse(Response.Status.BAD_REQUEST, 
                "Bad Request", 
                badRequestEx.getMessage());
        }

        if (exception instanceof NotFoundException notFoundEx) {
            return createErrorResponse(Response.Status.NOT_FOUND, 
                "Resource Not Found", 
                notFoundEx.getMessage());
        }

        // Custom application-specific exceptions
        if (exception instanceof AuthenticationException authEx) {
            return createErrorResponse(Response.Status.UNAUTHORIZED, 
                "Authentication Failed", 
                authEx.getMessage());
        }

        if (exception instanceof ElementNotFoundException elementNotFoundEx) {
            return createErrorResponse(Response.Status.NOT_FOUND, 
                "Element Not Found", 
                elementNotFoundEx.getMessage());
        }

        if (exception instanceof ElementExistException elementExistsEx) {
            return createErrorResponse(Response.Status.CONFLICT, 
                "Element Already Exists", 
                elementExistsEx.getMessage());
        }

        // Fallback for unexpected exceptions
        LOGGER.log(Level.SEVERE, "Unhandled exception", exception);
        return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
            "Unexpected Error", 
            "An unexpected error occurred");
    }

    private Response handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (v1, v2) -> v1 // in case of duplicate keys, keep the first value
            ));

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(
                LocalDateTime.now(), 
                "Validation Failed", 
                "One or more fields are invalid", 
                errors, 
                getRequestUri()
            ))
            .build();
    }

    private Response createErrorResponse(Response.Status status, String title, String message) {
        return Response.status(status)
            .entity(new ErrorResponse(
                LocalDateTime.now(), 
                "Error", 
                title, 
                message, 
                getRequestUri()
            ))
            .build();
    }

    private String getRequestUri() {
        // This is a placeholder. In a real Jakarta EE application, 
        // you might inject HttpServletRequest or use a context provider
        return headers != null ? 
            headers.getHeaderString("Referer") : 
            "Unknown Request URI";
    }
}