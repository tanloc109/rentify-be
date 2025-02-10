package com.rentify.auth.rest;

import com.rentify.auth.contants.AuthResponseMessage;
import com.rentify.auth.dto.LoginRequestDTO;
import com.rentify.auth.dto.RegisterRequestDTO;
import com.rentify.auth.dto.RegisterValidationResponseDTO;
import com.rentify.auth.service.AuthService;
import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.response.ResponseBody;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("auth")
@Tag(name = "Authentication", description = "Operations related to authentication")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class AuthRest {

    @Inject
    private AuthService authService;

    @POST
    @Path("login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    public Response login(@Valid LoginRequestDTO loginDTO) {
        return Response.ok().entity(new ResponseBody(
                AuthResponseMessage.LOGIN_SUCCESSFULLY,
                authService.login(loginDTO)
        )).build();
    }

    @POST
    @Path("register")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response register(RegisterRequestDTO requestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody(
                AuthResponseMessage.REGISTER_SUCCESSFULLY,
                authService.register(requestDTO)
        )).build();
    }


}
