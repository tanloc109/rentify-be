package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dto.paging.PagingResponse;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/api/v1/purposes")
@Tag(name = "Purpose", description = "Purpose Management Operations")
public class VaccineUseController {
    private static final Logger LOGGER = Logger.getLogger(VaccineUseController.class.getName());

    @Inject
    private VaccineUseService vaccineUseService;

    @Inject
    private VaccineUseMapper vaccineUseMapper;

    @Context
    private UriInfo uriInfo;

    private int getParamValue(String paramName, int defaultValue) {
        String paramValue = uriInfo.getQueryParameters().getFirst(paramName);
        return paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
    }

    @GET
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all purposes", description = "Retrieves all purposes, with optional pagination")
    public Response getAllPurposes(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = vaccineUseService.getAllPurposes(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @GET
    @Path("/active")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all active purposes", description = "Retrieves all purposes with active status")
    public Response getAllPurposesActive(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = vaccineUseService.getAllPurposesActive(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
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

        return results.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Failed", "Failed to retrieve purposes", null)
        ).build()
                : Response.ok(
                new ObjectResponse("Success", "Successfully retrieved purposes", results)
        ).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search vaccine uses", description = "Search vaccine uses by name")
    public Response searchVaccines(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("sortBy") String sortBy
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = vaccineUseService.searchVaccineUses(
                resolvedCurrentPage, resolvedPageSize, name, sortBy
        );

        List<?> data = (List<?>) results.getData();
        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @POST
    @Path("/{purpose-id}/restore")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Restore purpose", description = "Restore a deleted purpose")
    public Response unDeletePurposeByID(@PathParam("purpose-id") int purposeID) {
        try {
            VaccineUseResponseDTO result = vaccineUseService.undeletePurpose(purposeID);
            return Response.ok(
                    new ObjectResponse("Success", "Purpose restored successfully", result)
            ).build();
        } catch (ElementNotFoundException | UnchangedStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to restore purpose: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error restoring purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to restore purpose", null)
            ).build();
        }
    }

    @GET
    @Path("/{purpose-id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get purpose by ID", description = "Retrieve a purpose by its ID")
    public Response getPurposeByID(@PathParam("purpose-id") int purposeID) {
        VaccineUse vaccineUse = vaccineUseService.findById(purposeID);
        return vaccineUse != null
                ? Response.ok(
                new ObjectResponse("Success", "Purpose retrieved successfully",
                        vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse))
        ).build()
                : Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Fail", "Failed to retrieve purpose", null)
        ).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create purpose", description = "Create a new purpose")
    public Response createPurpose(@Valid VaccineUseCreateRequest vaccineUseCreateRequest) {
        try {
            VaccineUseResponseDTO result = vaccineUseService.createPurpose(vaccineUseCreateRequest);
            return Response.ok(
                    new ObjectResponse("Success", "Purpose created successfully", result)
            ).build();
        } catch (ElementExistException e) {
            LOGGER.log(Level.SEVERE, "Error creating purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to create purpose: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to create purpose", null)
            ).build();
        }
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
        try {
            VaccineUseResponseDTO purposeResponseDTO = vaccineUseService.updatePurpose(
                    vaccineUseUpdateRequest, purposeID
            );

            return purposeResponseDTO != null
                    ? Response.ok(
                    new ObjectResponse("Success", "Purpose updated successfully", purposeResponseDTO)
            ).build()
                    : Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update purpose. Purpose is null", null)
            ).build();
        } catch (ElementExistException | ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error updating purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update purpose: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update purpose", null)
            ).build();
        }
    }

    @DELETE
    @Path("/{purpose-id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete purpose", description = "Delete a purpose by setting deleted flag")
    public Response deletePurposeByID(@PathParam("purpose-id") int purposeID) {
        try {
            VaccineUseResponseDTO results = vaccineUseService.deletePurpose(purposeID);
            return Response.ok(
                    new ObjectResponse("Success", "Purpose deleted successfully", results)
            ).build();
        } catch (ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error deleting purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to delete purpose: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting purpose", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to delete purpose", null)
            ).build();
        }
    }
}