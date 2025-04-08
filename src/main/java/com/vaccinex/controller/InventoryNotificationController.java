package com.vaccinex.controller;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.VaccineInventoryAlert;
import com.vaccinex.service.AppointmentVerificationService;
import com.vaccinex.service.VaccineInventoryNotificationService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/inventory")
@Tag(name = "Inventory", description = "Inventory Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class InventoryNotificationController {

    @Inject
    private AppointmentVerificationService appointmentVerificationService;

    @Inject
    private VaccineInventoryNotificationService inventoryNotificationService;

    @GET
    @Path("/verify-appointment")
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyAppointmentAvailability(
            @QueryParam("vaccineId") Integer vaccineId,
            @QueryParam("appointmentDate") String appointmentDateStr
    ) {
        // Parse the date string to LocalDateTime
        LocalDateTime appointmentDate = LocalDateTime.parse(
                appointmentDateStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Appointment verification completed")
                        .data(appointmentVerificationService.verifyAppointmentAvailability(vaccineId, appointmentDate))
                        .build()
        ).build();
    }

    @GET
    @Path("/vaccine-alerts")
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVaccineInventoryAlerts(@QueryParam("days") Integer days) {
        List<VaccineInventoryAlert> alerts = inventoryNotificationService.getVaccineInventoryAlerts(days);

        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Vaccine inventory alerts generated successfully")
                        .data(alerts)
                        .build()
        ).build();
    }
}