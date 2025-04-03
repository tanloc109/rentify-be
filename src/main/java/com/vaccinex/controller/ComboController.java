package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.mapper.ComboMapper;
import com.vaccinex.pojo.Combo;

import com.vaccinex.service.ComboService;
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

@Path("/combos")
@Tag(name = "Combo", description = "Combo Management Operations")
public class ComboController {
    private static final Logger LOGGER = Logger.getLogger(ComboController.class.getName());

    @Inject
    private ComboService comboService;

    @Inject
    private ComboMapper comboMapper;

    @Context
    private UriInfo uriInfo;

    private int getParamValue(String paramName, int defaultValue) {
        String paramValue = uriInfo.getQueryParameters().getFirst(paramName);
        return paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
    }

    @GET
    @Operation(summary = "Get all combos", description = "Retrieves all combos, with optional pagination")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCombos(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = comboService.getAllCombos(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @GET
    @Path("/active")
    @Operation(summary = "Get all active combos", description = "Retrieves all combos have status is active")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCombosActive(
            @QueryParam("currentPage") Integer currentPage,
            @QueryParam("pageSize") Integer pageSize
    ) {
        int resolvedCurrentPage = currentPage != null ? currentPage : 1;
        int resolvedPageSize = pageSize != null ? pageSize : 10;

        PagingResponse results = comboService.getAllCombosActive(resolvedCurrentPage, resolvedPageSize);
        List<?> data = (List<?>) results.getData();

        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search active vaccine combos", description = "Retrieves vaccineCombos filtered by various parameters")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
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

        PagingResponse results = comboService.searchVaccineCombos(
                resolvedCurrentPage, resolvedPageSize, name, price,
                resolvedMinAge, resolvedMaxAge, sortBy
        );

        List<?> data = (List<?>) results.getData();
        return data.isEmpty()
                ? Response.status(Response.Status.BAD_REQUEST).entity(results).build()
                : Response.ok(results).build();
    }

    @POST
    @Path("/{combo-id}/restore")
    @Operation(summary = "Restore combo", description = "Restore combo by combo id set delete = false")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response unDeleteComboByID(@PathParam("combo-id") int comboID) {
        try {
            ComboResponseDTO combo = comboService.undeleteCombo(comboID);
            return Response.ok(
                    new ObjectResponse("Success", "Undelete combo successfully", combo)
            ).build();
        } catch (ElementNotFoundException | UnchangedStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Undelete combo failed: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error undeleting combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Undelete combo failed", null)
            ).build();
        }
    }

    @GET
    @Path("/{combo-id}")
    @Operation(summary = "Get combo by id", description = "Get combo by id")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComboByID(@PathParam("combo-id") int comboID) {
        Combo combo = comboService.findById(comboID);
        return combo != null
                ? Response.ok(
                new ObjectResponse("Success", "Get combo by ID successfully",
                        comboMapper.comboToComboResponseDTO(combo))
        ).build()
                : Response.status(Response.Status.BAD_REQUEST).entity(
                new ObjectResponse("Fail", "Get combo by ID failed", null)
        ).build();
    }

    @POST
    @Operation(summary = "Create combo", description = "Create combo")
    @RolesAllowed({"ADMIN"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCombo(@Valid VaccineComboCreateRequest vaccineComboCreateRequest) {
        try {
            ComboResponseDTO comboResponseDTO = comboService.createCombo(vaccineComboCreateRequest);
            return Response.ok(
                    new ObjectResponse("Success", "Combo created successfully", comboResponseDTO)
            ).build();
        } catch (BadRequestException | ElementExistException e) {
            LOGGER.log(Level.SEVERE, "Error creating combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo creation failed: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo creation failed", null)
            ).build();
        }
    }

    @PUT
    @Path("/{combo-id}")
    @Operation(summary = "Update combo by id", description = "Update combo by id")
    @RolesAllowed({"ADMIN"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCombo(
            @PathParam("combo-id") int comboID,
            VaccineComboUpdateRequest vaccineComboUpdateRequest
    ) {
        try {
            ComboResponseDTO comboResponseDTO = comboService.updateCombo(vaccineComboUpdateRequest, comboID);
            return comboResponseDTO != null
                    ? Response.ok(
                    new ObjectResponse("Success", "Combo updated successfully", comboResponseDTO)
            ).build()
                    : Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo update failed. No valid combo found", null)
            ).build();
        } catch (BadRequestException | ElementNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error updating combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo update failed: " + e.getMessage(), null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo update failed", null)
            ).build();
        }
    }

    @DELETE
    @Path("/{combo-id}")
    @Operation(summary = "Delete combo by id", description = "Delete combo by id and set deleted = true")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteComboByID(@PathParam("combo-id") int comboID) {
        try {
            Combo combo = comboService.findById(comboID);
            if (combo != null) {
                combo.setDeleted(true);
                return Response.ok(
                        new ObjectResponse("Success", "Combo deleted successfully",
                                comboMapper.comboToComboResponseDTO(comboService.save(combo)))
                ).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo deletion failed", null)
            ).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting combo", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(
                    new ObjectResponse("Fail", "Combo deletion failed", null)
            ).build();
        }
    }

    @GET
    @Path("/v2")
    @RolesAllowed({"USER", "DOCTOR", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCombosV2(
            @QueryParam("pageNo") @DefaultValue("1") Integer pageNo,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("params") String params,
            @QueryParam("sortBy") @DefaultValue("id") String sortBy
    ) {
        return Response.ok(
                comboService.getAllCombosV2(PagingRequest.builder()
                        .pageNo(pageNo)
                        .pageSize(pageSize)
                        .params(params)
                        .sortBy(sortBy)
                        .build())
        ).build();
    }
}