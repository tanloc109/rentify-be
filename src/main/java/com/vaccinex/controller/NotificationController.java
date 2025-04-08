package com.vaccinex.controller;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.NotificationService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Path("/notifications")
@Tag(name = "Notification", description = "Notification Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class NotificationController {
    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());

    @Inject
    private NotificationService notificationService;

    @GET
    @Path("/{userId}")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(
            @PathParam("userId") Integer userId
    ) {
        LOGGER.info("Fetching notifications for user: " + userId);

        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Notifications retrieved successfully")
                        .data(notificationService.getNotificationsByUserId(userId))
                        .build()
        ).build();
    }
}