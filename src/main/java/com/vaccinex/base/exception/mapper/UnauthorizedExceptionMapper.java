package com.vaccinex.base.exception.mapper;

import com.vaccinex.base.exception.UnauthorizedException;
import com.vaccinex.base.response.ResponseBody;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ResponseBody.builder().message(e.getMessage()).build())
                .build();
    }
}
