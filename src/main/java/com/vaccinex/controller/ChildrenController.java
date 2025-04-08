package com.vaccinex.controller;

import com.vaccinex.base.exception.ParseEnumException;
import com.vaccinex.dto.request.ChildrenRequestDTO;
import com.vaccinex.dto.response.ChildrenResponseDTO;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.ChildrenService;
import com.vaccinex.service.VaccineScheduleService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/children")
@Tag(name = "Children", description = "Children Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class ChildrenController {

    @Inject
    private ChildrenService childService;

    @Inject
    private VaccineScheduleService vaccineScheduleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getAllChildren() {
        List<ChildrenResponseDTO> children = childService.findAll();
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Successfully retrieved all children")
                        .data(children)
                        .build()
        ).build();
    }

    @GET
    @Path("/{childrenId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "DOCTOR", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getChildById(@PathParam("childrenId") Integer childrenId) {
        ChildrenResponseDTO child = childService.findById(childrenId);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Successfully retrieved child details")
                        .data(child)
                        .build()
        ).build();
    }

    @GET
    @Path("/parent")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getChildrenByParentId(@Context HttpServletRequest request) {
        List<ChildrenResponseDTO> children = childService.findByParentId(request);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Successfully retrieved children for the parent")
                        .data(children)
                        .build()
        ).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response createNewChild(
            @Valid ChildrenRequestDTO dto,
            @Context HttpServletRequest request
    ) throws ParseEnumException {
        System.out.println("Creating new child with request: " + dto);
        ChildrenResponseDTO createdChild = childService.createChild(dto, request);
        return Response.status(Response.Status.CREATED).entity(
                ObjectResponse.builder()
                        .status(Response.Status.CREATED.toString())
                        .message("Child added to the system successfully")
                        .data(createdChild)
                        .build()
        ).build();
    }

    @PUT
    @Path("/{childrenId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response updateChild(
            @PathParam("childrenId") Integer childrenId,
            @Valid ChildrenRequestDTO dto,
            @Context HttpServletRequest request
    ) {
        ChildrenResponseDTO updatedChild = childService.update(childrenId, dto, request);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Child information updated successfully")
                        .data(updatedChild)
                        .build()
        ).build();
    }

    @DELETE
    @Path("/{childrenId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response deleteChildById(@PathParam("childrenId") Integer childrenId) {
        childService.deleteById(childrenId);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Child deleted successfully")
                        .build()
        ).build();
    }

    @GET
    @Path("/{childId}/schedules/availability")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getEarliestPossibleSchedule(@PathParam("childId") Integer childId) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Successfully retrieved earliest possible schedule")
                        .data(childService.getEarliestPossibleSchedule(childId))
                        .build()
        ).build();
    }

    @GET
    @Path("/{childId}/schedules/draft")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getDraftSchedules(@PathParam("childId") Integer childId) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Draft schedules retrieved successfully")
                        .data(vaccineScheduleService.getDrafts(childId))
                        .build()
        ).build();
    }

    @DELETE
    @Path("/{childId}/schedules/drafts")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "bearerAuth")
    public Response deleteDraftSchedules(@PathParam("childId") Integer childId) {
        vaccineScheduleService.deleteDraftSchedules(childId);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Draft schedules deleted successfully")
                        .data(null)
                        .build()
        ).build();
    }
}