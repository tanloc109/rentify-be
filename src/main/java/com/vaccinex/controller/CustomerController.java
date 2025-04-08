package com.vaccinex.controller;

import com.vaccinex.dto.request.CustomerUpdateProfile;
import com.vaccinex.dto.request.FeedbackRequestDTO;
import com.vaccinex.dto.request.ReactionCreateRequest;
import com.vaccinex.dto.response.ChildrenResponseDTO;
import com.vaccinex.dto.response.CustomerInfoResponse;
import com.vaccinex.dto.response.CustomerScheduleResponse;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.CustomerService;
import com.vaccinex.service.VaccineScheduleService;
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
import java.util.logging.Logger;

@Path("/customers")
@Tag(name = "Customer", description = "Customer Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class CustomerController {
    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());

    @Inject
    private CustomerService parentService;

    @Inject
    private VaccineScheduleService vaccineScheduleService;

    @GET
    @Path("/{customerId}/children")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChildrenByParentId(@PathParam("customerId") Integer customerId) {
        List<ChildrenResponseDTO> children = parentService.getChildByParentId(customerId);
        return Response.ok(children).build();
    }

    @PUT
    @Path("/schedules/{scheduleId}/feedback")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateFeedback(
            @PathParam("scheduleId") Integer scheduleId,
            @Valid FeedbackRequestDTO feedbackRequestDTO
    ) {
        parentService.updateFeedback(feedbackRequestDTO, scheduleId);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Feedback updated successfully")
                        .build()
        ).build();
    }

    @POST
    @Path("/schedules/{scheduleId}/reaction")
    @RolesAllowed({"DOCTOR", "USER"})
    @SecurityRequirement(name = "bearerAuth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReaction(
            @PathParam("scheduleId") Integer scheduleId,
            @Valid ReactionCreateRequest reactionCreateRequest
    ) {
        return Response.status(Response.Status.CREATED).entity(
                ObjectResponse.builder()
                        .status(Response.Status.CREATED.toString())
                        .message("Reaction created successfully")
                        .data(parentService.createReactionDetail(reactionCreateRequest, scheduleId))
                        .build()
        ).build();
    }

    @GET
    @Path("/{customerId}/schedules")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedules(@PathParam("customerId") Integer customerId) {
        List<CustomerScheduleResponse> schedules = vaccineScheduleService.getVaccinesByCustomer(customerId);

        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Schedules retrieved successfully")
                        .data(schedules)
                        .build()
        ).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") Integer id) {
        CustomerInfoResponse user = parentService.findUserById(id);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Customer retrieved successfully")
                        .data(user)
                        .build()
        ).build();
    }

    @PUT
    @Path("/{customerId}")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(
            @PathParam("customerId") Integer customerId,
            @Valid CustomerUpdateProfile request
    ) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Customer updated successfully")
                        .data(parentService.updateCustomer(customerId, request))
                        .build()
        ).build();
    }
}