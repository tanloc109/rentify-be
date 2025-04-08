package com.vaccinex.controller;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.ComboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.logging.Logger;

@Path("/combos")
@Tag(name = "Combo", description = "Combo Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class ComboController {
    private static final Logger LOGGER = Logger.getLogger(ComboController.class.getName());

    @Inject
    private ComboService comboService;

    @GET
    @Operation(summary = "Get all combos", description = "Retrieves all combos")
    @RolesAllowed({"USER","ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCombos() {
        List<ComboResponseDTO> results = comboService.getAllCombos();

        return !results.isEmpty()
                ? Response.ok(new ObjectResponse("Success", "Retrieved all combos successfully", results)).build()
                : Response.status(Response.Status.NOT_FOUND)
                .entity(new ObjectResponse("Fail", "No combos found", null))
                .build();
    }

    @GET
    @Path("/active")
    @Operation(summary = "Get all active combos", description = "Retrieves all combos that are active")
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCombosActive() {
        List<ComboResponseDTO> results = comboService.getAllCombosActive();

        return !results.isEmpty()
                ? Response.ok(new ObjectResponse("Success", "Retrieved all active combos successfully", results)).build()
                : Response.status(Response.Status.NOT_FOUND)
                .entity(new ObjectResponse("Fail", "No active combos found", null))
                .build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search active vaccine combos", description = "Retrieves combos filtered by various parameters")
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchVaccines(
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("price") @DefaultValue("") String price,
            @QueryParam("minAge") @DefaultValue("0") Integer minAge,
            @QueryParam("maxAge") @DefaultValue("0") Integer maxAge
    ) {
        List<ComboResponseDTO> results = comboService.searchVaccineCombos(name, price, minAge, maxAge);

        return !results.isEmpty()
                ? Response.ok(new ObjectResponse("Success", "Search completed successfully", results)).build()
                : Response.status(Response.Status.NOT_FOUND)
                .entity(new ObjectResponse("Fail", "No combos found matching the criteria", null))
                .build();
    }

    @POST
    @Path("/{combo-id}/restore")
    @Operation(summary = "Restore combo", description = "Restore combo by setting deleted = false")
    @RolesAllowed({"ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unDeleteComboByID(@PathParam("combo-id") int comboID) {
        ComboResponseDTO combo = comboService.undeleteCombo(comboID);
        return Response.ok(new ObjectResponse("Success", "Combo restored successfully", combo)).build();
    }

    @GET
    @Path("/{combo-id}")
    @Operation(summary = "Get combo by id", description = "Get combo by id")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComboByID(@PathParam("combo-id") int comboID) {
        ComboResponseDTO combo = comboService.toComboResponseDTO(comboService.getComboById(comboID));
        return Response.ok(new ObjectResponse("Success", "Retrieved combo successfully", combo)).build();
    }

    @POST
    @Operation(summary = "Create combo", description = "Create combo")
    @RolesAllowed({"ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCombo(@Valid VaccineComboCreateRequest vaccineComboCreateRequest) {
        ComboResponseDTO comboResponseDTO = comboService.createCombo(vaccineComboCreateRequest);
        return Response.status(Response.Status.CREATED)
                .entity(new ObjectResponse("Success", "Combo created successfully", comboResponseDTO))
                .build();
    }

    @PUT
    @Path("/{combo-id}")
    @Operation(summary = "Update combo by id", description = "Update combo by id")
    @RolesAllowed({"ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCombo(
            @PathParam("combo-id") int comboID,
            VaccineComboUpdateRequest vaccineComboUpdateRequest
    ) {
        ComboResponseDTO comboResponseDTO = comboService.updateCombo(vaccineComboUpdateRequest, comboID);
        return Response.ok(new ObjectResponse("Success", "Combo updated successfully", comboResponseDTO)).build();
    }

    @DELETE
    @Path("/{combo-id}")
    @Operation(summary = "Delete combo by id", description = "Delete combo by id and set deleted = true")
    @RolesAllowed({"ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteComboByID(@PathParam("combo-id") int comboID) {
        ComboResponseDTO deletedCombo = comboService.deleteCombo(comboID);
        return Response.ok(new ObjectResponse("Success", "Combo deleted successfully", deletedCombo)).build();
    }

    @Provider
    public static class ElementNotFoundExceptionMapper implements ExceptionMapper<ElementNotFoundException> {
        @Override
        public Response toResponse(ElementNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ObjectResponse("Fail", e.getMessage(), null))
                    .build();
        }
    }

    @Provider
    public static class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
        @Override
        public Response toResponse(BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Fail", e.getMessage(), null))
                    .build();
        }
    }

    @Provider
    public static class ElementExistExceptionMapper implements ExceptionMapper<ElementExistException> {
        @Override
        public Response toResponse(ElementExistException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ObjectResponse("Fail", e.getMessage(), null))
                    .build();
        }
    }

    @Provider
    public static class UnchangedStateExceptionMapper implements ExceptionMapper<UnchangedStateException> {
        @Override
        public Response toResponse(UnchangedStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ObjectResponse("Fail", e.getMessage(), null))
                    .build();
        }
    }
}