package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineCreateRequest;
import com.vaccinex.dto.request.VaccineUpdateRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.Vaccine;

import com.vaccinex.service.BatchService;
import com.vaccinex.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
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

@Path("/vaccines")
@Tag(name = "Vaccine", description = "Vaccine Management Operations")
public class VaccineController {
    private static final Logger LOGGER = Logger.getLogger(VaccineController.class.getName());

    @Inject
    private VaccineService vaccineService;

    @Inject
    private VaccineMapper vaccineMapper;

    @Inject
    private BatchService batchService;

    @Context
    private UriInfo uriInfo;

    private int getParamValue(String paramName, int defaultValue) {
        String paramValue = uriInfo.getQueryParameters().getFirst(paramName);
        return paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all vaccines", description = "Retrieves all vaccines, with optional pagination")
    public Response getAllVaccines(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = vaccineService.getAllVaccines(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @GET
    @Path("/non-paging")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all vaccines not paging", description = "Retrieves all vaccines not paging")
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
    @Path("/active")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all active vaccines", description = "Retrieves all vaccines with active status")
    public Response getAllVaccinesActive(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = vaccineService.getAllVaccineActive(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @GET
    @Path("/search")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search active vaccines", description = "Search vaccines by various criteria")
    public Response searchVaccines(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("purpose") @DefaultValue("") String purpose,
            @QueryParam("price") @DefaultValue("") String price,
            @QueryParam("minAge") Integer minAge,
            @QueryParam("maxAge") Integer maxAge,
            @QueryParam("sortBy") String sortBy
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;
        int resolvedMinAge = minAge != null ? minAge : 0;
        int resolvedMaxAge = maxAge != null ? maxAge : 0;

        PagingResponse results = vaccineService.searchVaccines(
                resolvedCurrentPage, resolvedPageSize, name, purpose, price,
                resolvedMinAge, resolvedMaxAge, sortBy
        );

        List<?> data = (List<?>) results.getData();
        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @POST
    @Path("/{vaccine-id}/restore")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Restore vaccine", description = "Restore a deleted vaccine")
    public Response unDeleteVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        try {
            VaccineResponseDTO vaccine = vaccineService.undeleteVaccine(vaccineID);
            return Response.ok(
                    new ObjectResponse("Success", "Vaccine restored successfully", vaccine)
            ).build();
        } catch (ElementNotFoundException | UnchangedStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to restore vaccine: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error restoring vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to restore vaccine", null)
            ).build();
        }
    }

    @GET
    @Path("/{vaccine-id}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get vaccine by ID", description = "Retrieve a vaccine by its ID")
    public Response getVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        Vaccine vaccine = vaccineService.findById(vaccineID);
        return vaccine != null
                ? Response.ok(
                new ObjectResponse("Success", "Vaccine retrieved successfully",
                        vaccineMapper.vaccineToVaccineResponseDTO(vaccine))
        ).build()
                : Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Fail", "Failed to retrieve vaccine", null)
        ).build();
    }

    @POST
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create vaccine", description = "Create a new vaccine")
    public Response createVaccine(@Valid VaccineCreateRequest vaccineCreateRequest) {
        try {
            VaccineResponseDTO vaccineResponseDTO = vaccineService.createVaccine(vaccineCreateRequest);
            return Response.ok(
                    new ObjectResponse("Success", "Vaccine created successfully", vaccineResponseDTO)
            ).build();
        } catch (BadRequestException | ElementExistException | ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error creating vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to create vaccine: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to create vaccine", null)
            ).build();
        }
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
        try {
            VaccineResponseDTO vaccineResponseDTO = vaccineService.updateVaccine(vaccineUpdateRequest, vaccineID);
            return vaccineResponseDTO != null
                    ? Response.ok(
                    new ObjectResponse("Success", "Vaccine updated successfully", vaccineResponseDTO)
            ).build()
                    : Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update vaccine. Vaccine is null", null)
            ).build();
        } catch (BadRequestException | ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error updating vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update vaccine: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to update vaccine", null)
            ).build();
        }
    }

    @DELETE
    @Path("/{vaccine-id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete vaccine", description = "Delete a vaccine by setting deleted flag")
    public Response deleteVaccineByID(@PathParam("vaccine-id") int vaccineID) {
        try {
            VaccineResponseDTO results = vaccineService.deleteVaccine(vaccineID);
            return Response.ok(
                    new ObjectResponse("Success", "Vaccine deleted successfully", results)
            ).build();
        } catch (ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error deleting vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to delete vaccine: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting vaccine", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Failed to delete vaccine", null)
            ).build();
        }
    }

    @GET
    @Path("/v2")
    @RolesAllowed({"ADMIN", "STAFF"})
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