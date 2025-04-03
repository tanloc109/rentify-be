package com.vaccinex.controller;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.BatchJobService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/batch-jobs")
@Tag(name = "Batch Job", description = "Batch Job Management Operations")
public class BatchJobController {

    @Inject
    private BatchJobService batchJobService;

    @GET
    @Path("/reminders")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response sendMail() {
        batchJobService.remindVaccineSchedules();
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Vaccine schedule reminders sent successfully")
                        .build()
        ).build();
    }

    @GET
    @Path("/batch-assignment")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response assignBatches() {
        batchJobService.assignBatchToSchedules();
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Batches assigned to schedules successfully")
                        .build()
        ).build();
    }
}