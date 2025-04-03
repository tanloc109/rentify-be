package com.vaccinex.controller;

import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.dto.request.VaccineUseUpdateRequest;
import com.vaccinex.dto.request.VaccineUseCreateRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.mapper.VaccineUseMapper;
import com.vaccinex.pojo.VaccineUse;
import com.vaccinex.service.VaccineUseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/purposes")
@Tag(name = "Purpose", description = "Purpose Management Operations")
public class VaccineUseController {

    @Inject
    private VaccineUseService vaccineUseService;

    @Inject
    private VaccineUseMapper vaccineUseMapper;

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all purposes", description = "Retrieves all purposes")
    public Response getAllPurposes() {
        List<VaccineUseResponseDTO> results = vaccineUseService.getAllPurposes();
        return Response.ok(
                new ObjectResponse("Success", "Successfully retrieved purposes", results)
        ).build();
    }

    @GET
    @Path("/active")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all active purposes", description = "Retrieves all purposes with active status")
    public Response getAllPurposesActive() {
        List<VaccineUseResponseDTO> results = vaccineUseService.getAllPurposesActive();
        return Response.ok(
                new ObjectResponse("Success", "Successfully retrieved active purposes", results)
        ).build();
    }

    @GET
    @Path("/non-paging")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all purposes not paging", description = "Retrieves all purposes without pagination")
    public Response getAllPurposesNotPaging(
            @QueryParam("status") String status
    ) {
        List<VaccineUseResponseDTO> results = (status != null && status.equals("active"))
                ? vaccineUseService.getPurposesActive()
                : vaccineUseService.getPurposes();

        return Response.ok(
                new ObjectResponse("Success", "Successfully retrieved purposes", results)
        ).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search vaccine uses", description = "Search vaccine uses by name")
    public Response searchVaccines(
            @QueryParam("name") @DefaultValue("") String name
    ) {
        List<VaccineUseResponseDTO> results = vaccineUseService.searchVaccineUses(name);
        return Response.ok(
                new ObjectResponse("Success", "Successfully retrieved purposes", results)
        ).build();
    }

    @POST
    @Path("/{purpose-id}/restore")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Restore purpose", description = "Restore a deleted purpose")
    public Response unDeletePurposeByID(@PathParam("purpose-id") int purposeID) {
        VaccineUseResponseDTO result = vaccineUseService.undeletePurpose(purposeID);
        return Response.ok(
                new ObjectResponse("Success", "Purpose restored successfully", result)
        ).build();
    }

    @GET
    @Path("/{purpose-id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get purpose by ID", description = "Retrieve a purpose by its ID")
    public Response getPurposeByID(@PathParam("purpose-id") int purposeID) {
        VaccineUse vaccineUse = vaccineUseService.findById(purposeID).orElseThrow(() -> new IdNotFoundException("Cannot found Vaccine Use"));
        return Response.ok(
                new ObjectResponse("Success", "Purpose retrieved successfully",
                        vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse))
        ).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create purpose", description = "Create a new purpose")
    public Response createPurpose(@Valid VaccineUseCreateRequest vaccineUseCreateRequest) {
        VaccineUseResponseDTO result = vaccineUseService.createPurpose(vaccineUseCreateRequest);
        return Response.ok(
                new ObjectResponse("Success", "Purpose created successfully", result)
        ).build();
    }

    @PUT
    @Path("/{purpose-id}")
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update purpose", description = "Update an existing purpose")
    public Response updatePurpose(
            @PathParam("purpose-id") int purposeID,
            @Valid VaccineUseUpdateRequest vaccineUseUpdateRequest
    ) {
        VaccineUseResponseDTO purposeResponseDTO = vaccineUseService.updatePurpose(
                vaccineUseUpdateRequest, purposeID
        );
        return Response.ok(
                new ObjectResponse("Success", "Purpose updated successfully", purposeResponseDTO)
        ).build();
    }

    @DELETE
    @Path("/{purpose-id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete purpose", description = "Delete a purpose by setting deleted flag")
    public Response deletePurposeByID(@PathParam("purpose-id") int purposeID) {
        VaccineUseResponseDTO results = vaccineUseService.deletePurpose(purposeID);
        return Response.ok(
                new ObjectResponse("Success", "Purpose deleted successfully", results)
        ).build();
    }
}