package com.vaccinex.controller;

import com.vaccinex.dto.response.DoctorResponseDTO;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/doctors")
@Tag(name = "Doctors", description = "Operations related to doctors")
@ApplicationScoped
public class AccountController {

    @Inject
    private AccountService accountService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF", "USER", "DOCTOR"})
    @Operation(
            summary = "Get All Doctors",
            description = "Retrieve a list of all doctors",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved doctors"),
                    @ApiResponse(responseCode = "403", description = "Access forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Response getAllDoctors() {
        List<DoctorResponseDTO> doctors = accountService.findAllDoctors();

        ObjectResponse response = ObjectResponse.builder()
                .status(Response.Status.OK.toString())
                .message("Get All Doctors Successfully")
                .data(doctors)
                .build();

        return Response.ok(response).build();
    }
}