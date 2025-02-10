package com.rentify.base.exception.mapper;

import com.rentify.base.exception.IdNotFoundException;
import com.rentify.base.response.ResponseBody;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IdNotFoundExceptionMapper implements ExceptionMapper<IdNotFoundException> {

    @Override
    public Response toResponse(IdNotFoundException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ResponseBody.builder().message(e.getMessage()).build())
                .build();
    }
}
