package com.vaccinex.controller;

import com.vaccinex.dto.request.BatchCreateRequest;
import com.vaccinex.dto.request.BatchUpdateRequest;
import com.vaccinex.dto.request.VaccineReturnRequest;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.BatchService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Path("/batches")
@Tag(name = "Batch", description = "Batch Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class BatchController {

    @Inject
    private BatchService batchService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF", "DOCTOR"})
    @SecurityRequirement(name = "bearerAuth")
    public Response getAllBatches(
            @Context UriInfo uriInfo,
            @QueryParam("pageNo") @DefaultValue("1") Integer pageNo,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("params") String params,
            @QueryParam("sortBy") @DefaultValue("id") String sortBy
    ) {
        return Response
                .status(Response.Status.OK)
                .entity(batchService.getAllBatches())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    public Response createBatch(BatchCreateRequest batchCreateRequest) {
        batchService.createBatch(batchCreateRequest);
        return Response
                .status(Response.Status.CREATED)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.CREATED.toString())
                                .message("Vaccine batch created successfully")
                                .build()
                )
                .build();
    }

    @PUT
    @Path("/{batchId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    public Response updateBatch(
            @PathParam("batchId") Integer batchId,
            BatchUpdateRequest request
    ) {
        batchService.updateBatch(batchId, request);
        return Response
                .status(Response.Status.OK)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.OK.toString())
                                .message("Batch updated successfully")
                                .build()
                )
                .build();
    }

    @DELETE
    @Path("/{batchId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    public Response deleteBatch(
            @PathParam("batchId") Integer batchId
    ) {
        batchService.deleteBatch(batchId);
        return Response
                .status(Response.Status.OK)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.OK.toString())
                                .message("Batch deleted successfully")
                                .data(null)
                                .build()
                )
                .build();
    }

    @PUT
    @Path("/returns")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "STAFF"})
    @SecurityRequirement(name = "bearerAuth")
    public Response returnVaccine(VaccineReturnRequest request) {
        batchService.returnVaccine(request);
        return Response
                .ok(
                        ObjectResponse.builder()
                                .status(Response.Status.OK.toString())
                                .message("Excess vaccines returned successfully")
                                .data(null)
                                .build()
                )
                .build();
    }
}