package com.rentify.base.exception.mapper;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.response.ErrorResponseBody;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException e) {
        ErrorResponseBody.ErrorResponseBodyBuilder responseBodyBuilder = ErrorResponseBody.builder().message(e.getMessage());
        if (e.getErrors() != null) {
            responseBodyBuilder.errors(e.getErrors());
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(responseBodyBuilder.build())
                .build();
    }
}