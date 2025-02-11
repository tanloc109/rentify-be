package com.rentify.base.exception.mapper;

import com.rentify.base.exception.ForbiddenException;
import com.rentify.base.response.ResponseBody;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(ResponseBody.builder().message(e.getMessage()).build())
                .build();
    }
}
