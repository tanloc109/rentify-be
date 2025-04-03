package com.vaccinex.controller;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Path("/notifications")
@Tag(name = "Notification", description = "Notification Management Operations")
public class NotificationController {
    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());

    @Inject
    private NotificationService notificationService;

    @GET
    @Path("/{userId}")
    @RolesAllowed("USER")
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