package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.dto.request.AccountLoginRequest;
import com.vaccinex.dto.request.AccountRegisterRequest;
import com.vaccinex.dto.response.AccountDTO;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.TokenResponse;
import com.vaccinex.service.AccountService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/authentication")
@Tag(name = "Authentication", description = "Authentication Management Operations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationController {

    @Inject
    private AccountService accountService;

    @POST
    @Path("/register")
    public Response userRegister(@Valid AccountRegisterRequest accountRegisterRequest) {
        try {
            AccountDTO account = accountService.registerAccount(accountRegisterRequest);
            return Response.ok(new ObjectResponse("Success", "Account registration successful", account)).build();
        } catch (Exception e) {
            log.error("Error registering user", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Fail", "Account registration failed", null))
                    .build();
        }
    }

    @POST
    @Path("/refresh-token")
    public Response refreshToken(@Context HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");
        TokenResponse tokenResponse = accountService.refreshToken(refreshToken);

        return tokenResponse.getCode().equals("Success")
                ? Response.ok(tokenResponse).build()
                : Response.status(Response.Status.UNAUTHORIZED).entity(tokenResponse).build();
    }

    @POST
    @Path("/login")
    public Response loginPage(@Valid AccountLoginRequest accountLoginRequest) {
        try {
            TokenResponse tokenResponse = accountService.login(accountLoginRequest.getEmail(), accountLoginRequest.getPassword());

            return tokenResponse.getCode().equals("Success")
                    ? Response.ok(tokenResponse).build()
                    : Response.status(Response.Status.UNAUTHORIZED).entity(tokenResponse).build();
        } catch (Exception e) {
            log.error("Cannot login: {}", e.toString());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(TokenResponse.builder()
                            .code("FAILED")
                            .message("Login failed")
                            .build())
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response getLogout(@Context HttpServletRequest request) {
        try {
            boolean checkLogout = accountService.logout(request);
            return checkLogout
                    ? Response.ok(new ObjectResponse("Success", "Logout successful", null)).build()
                    : Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Failed", "Logout failed", null))
                    .build();
        } catch (ElementNotFoundException e) {
            log.error("Error during logout - not found: {}", e.toString());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Failed", "Logout failed - user not found", null))
                    .build();
        } catch (Exception e) {
            log.error("Error during logout: {}", e.toString());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Failed", "Logout failed", null))
                    .build();
        }
    }
}