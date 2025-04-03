package com.vaccinex.controller;

import com.vaccinex.dto.request.ExportVaccineRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;

@Path("/warehouses")
@Tag(name = "Warehouse", description = "Operations related to warehouse management")
@ApplicationScoped
public class WarehouseController {

    @Inject
    private WarehouseService warehouseService;

    @GET
    @Path("/reports")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("STAFF")
    @Operation(
            summary = "Get Vaccine Reports",
            description = "Generate reports on required vaccine doses for morning and afternoon shifts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vaccine reports retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Access forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Response getVaccineReports(
            @QueryParam("doctorId") Integer doctorId,
            @QueryParam("shift") String shift,
            @QueryParam("date") LocalDate date) {

        ObjectResponse response = ObjectResponse.builder()
                .status(Response.Status.OK.toString())
                .message("Vaccine dose report for morning/afternoon shift")
                .data(warehouseService.getVaccineReports(doctorId, shift, date))
                .build();

        return Response.ok(response).build();
    }

    @POST
    @Path("/export")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("STAFF")
    @Operation(
            summary = "Request Vaccine Export",
            description = "Create a vaccine export request (Deduct from batch → Create transaction)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Vaccine export request created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "403", description = "Access forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Response requestVaccineExport(ExportVaccineRequest request) throws BadRequestException {
        Object exportResult = warehouseService.requestVaccineExport(request);

        ObjectResponse response = ObjectResponse.builder()
                .status(Response.Status.CREATED.toString())
                .message("Vaccine export request created successfully")
                .data(exportResult)
                .build();

        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}