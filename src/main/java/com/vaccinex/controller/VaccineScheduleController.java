package com.vaccinex.controller;

import com.vaccinex.dto.request.VaccineDraftRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.VaccineScheduleService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/schedules")
@Tag(name = "Vaccine Schedule", description = "Vaccine Schedule Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class VaccineScheduleController {

    @Inject
    private VaccineScheduleService vaccineScheduleService;

    @GET
    @Path("/doctor/{doctorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDoctorSchedule(
            @PathParam("doctorId") Integer doctorId,
            @QueryParam("date") String dateStr
    ) {
        LocalDate date = LocalDate.parse(dateStr);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Doctor schedule retrieved successfully")
                        .data(vaccineScheduleService.getDoctorSchedule(doctorId, date))
                        .build()
        ).build();
    }

    @GET
    @Path("/{detailId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScheduleDetails(
            @PathParam("detailId") Integer detailId
    ) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Schedule details retrieved successfully")
                        .data(vaccineScheduleService.getScheduleDetails(detailId))
                        .build()
        ).build();
    }

    @PUT
    @Path("/{scheduleId}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTime(
            @PathParam("scheduleId") Integer scheduleId,
            @QueryParam("newDate") String newDateStr
    ) {
        LocalDateTime newDate = LocalDateTime.parse(newDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        vaccineScheduleService.updateSchedule(scheduleId, newDate);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Schedule time updated successfully")
                        .data(null)
                        .build()
        ).build();
    }

    @PUT
    @Path("/existing/{scheduleId}")
    @RolesAllowed({"USER", "ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExistingSchedule(
            @PathParam("scheduleId") Integer scheduleId,
            @QueryParam("newDate") String newDateStr
    ) {
        LocalDateTime newDate = LocalDateTime.parse(newDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        vaccineScheduleService.updateExistingSchedule(scheduleId, newDate);
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Existing schedule updated successfully")
                        .data(null)
                        .build()
        ).build();
    }

    @POST
    @Path("/draft")
    @RolesAllowed({"USER", "ADMIN"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response draftSchedule(
            @Valid VaccineDraftRequest request
    ) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Draft schedule created successfully")
                        .data(vaccineScheduleService.draftSchedule(request))
                        .build()
        ).build();
    }

    @PUT
    @Path("/doctor/{doctorId}/confirm/{scheduleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmVaccination(
            @PathParam("scheduleId") Integer scheduleId,
            @PathParam("doctorId") Integer doctorId
    ) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Vaccination confirmed successfully")
                        .data(vaccineScheduleService.confirmVaccination(scheduleId, doctorId))
                        .build()
        ).build();
    }

    @GET
    @Path("/doctor/{doctorId}/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistorySchedules(
            @PathParam("doctorId") Integer doctorId
    ) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Doctor schedule history retrieved successfully")
                        .data(vaccineScheduleService.getDoctorHistory(doctorId))
                        .build()
        ).build();
    }
}