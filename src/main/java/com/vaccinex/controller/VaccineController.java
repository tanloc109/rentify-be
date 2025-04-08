package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dto.request.VaccineCreateRequest;
import com.vaccinex.dto.request.VaccineUpdateRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.service.BatchService;
import com.vaccinex.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/vaccines")
@Tag(name = "Vaccine", description = "Vaccine Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class VaccineController {

    @Inject
    private VaccineService vaccineService;

    @Inject
    private VaccineMapper vaccineMapper;

    @Inject
    private BatchService batchService;

    @GET
    @Path("/active")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all active vaccines", description = "Retrieves all non-deleted vaccines")
    public Response getAllVaccinesActive() {
        List<VaccineResponseDTO> results = vaccineService.getAllVaccineActive();
        return results.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Failed", "Active vaccine retrieval unsuccessful", null)
        ).build()
                : Response.ok(
                new ObjectResponse("Success", "Active vaccine retrieval successful", results)
        ).build();
    }

    @GET
    @Path("/non-paging")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all vaccines", description = "Retrieves all vaccines")
    public Response getAllVaccinesNotPaging(
            @QueryParam("status") String status
    ) {
        List<VaccineResponseDTO> results = (status != null && status.equals("active"))
                ? vaccineService.getVaccinesActiveV2()
                : vaccineService.getVaccinesV2();

        return results.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Failed", "Vaccine retrieval unsuccessful", null)
        ).build()
                : Response.ok(
                new ObjectResponse("Success", "Vaccine retrieval successful", results)
        ).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search vaccines", description = "Search vaccines by various criteria")
    public Response searchVaccines(
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("purpose") @DefaultValue("") String purpose,
            @QueryParam("price") @DefaultValue("") String price,
            @QueryParam("minAge") Integer minAge,
            @QueryParam("maxAge") Integer maxAge
    ) {
        List<VaccineResponseDTO> results = vaccineService.searchVaccines(
                name, purpose, price, minAge, maxAge
        );

        return results.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Failed", "No vaccines found", null)
        ).build()
                : Response.ok(
                new ObjectResponse("Success", "Vaccines retrieved successfully", results)
        ).build();
    }

    @POST
    @Path("/{vaccine-id}/restore")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Restore vaccine", description = "Restore a deleted vaccine")
    public Response unDeleteVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        VaccineResponseDTO vaccine = vaccineService.undeleteVaccine(vaccineID);
        return Response.ok(
                new ObjectResponse("Success", "Vaccine restored successfully", vaccine)
        ).build();
    }

    @GET
    @Path("/{vaccine-id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get vaccine by ID", description = "Retrieve a vaccine by its ID")
    public Response getVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        Vaccine vaccine = vaccineService.findById(vaccineID).orElseThrow(() -> new IdNotFoundException("Cannot found Vaccine"));
        return Response.ok(
                new ObjectResponse("Success", "Vaccine retrieved successfully",
                        vaccineMapper.vaccineToVaccineResponseDTO(vaccine))
        ).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create vaccine", description = "Create a new vaccine")
    public Response createVaccine(@Valid VaccineCreateRequest vaccineCreateRequest) {
        VaccineResponseDTO vaccineResponseDTO = vaccineService.createVaccine(vaccineCreateRequest);
        return Response.ok(
                new ObjectResponse("Success", "Vaccine created successfully", vaccineResponseDTO)
        ).build();
    }

    @PUT
    @Path("/{vaccine-id}")
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update vaccine", description = "Update an existing vaccine")
    public Response updateVaccine(
            @PathParam("vaccine-id") int vaccineID,
            @Valid VaccineUpdateRequest vaccineUpdateRequest
    ) {
        VaccineResponseDTO vaccineResponseDTO = vaccineService.updateVaccine(vaccineUpdateRequest, vaccineID);
        return Response.ok(
                new ObjectResponse("Success", "Vaccine updated successfully", vaccineResponseDTO)
        ).build();
    }

    @DELETE
    @Path("/{vaccine-id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete vaccine", description = "Delete a vaccine by setting deleted flag")
    public Response deleteVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        VaccineResponseDTO results = vaccineService.deleteVaccine(vaccineID);
        return Response.ok(
                new ObjectResponse("Success", "Vaccine deleted successfully", results)
        ).build();
    }

    @GET
    @Path("/v2")
    @RolesAllowed({"USER","ADMIN", "STAFF"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVaccines() {
        return Response.ok(vaccineService.getVaccines()).build();
    }

    @GET
    @Path("/quantities")
    @RolesAllowed({"ADMIN", "STAFF"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuantityByVaccines() {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Vaccine quantities retrieved successfully")
                        .data(batchService.getQuantityOfVaccines())
                        .build()
        ).build();
    }
}