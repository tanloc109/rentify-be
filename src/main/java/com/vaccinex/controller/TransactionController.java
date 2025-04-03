package com.vaccinex.controller;

import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.logging.Logger;

@Path("/transactions")
@Tag(name = "Transactions", description = "Transaction Management Operations")
public class TransactionController {
    private static final Logger LOGGER = Logger.getLogger(TransactionController.class.getName());

    @Inject
    private TransactionService transactionService;

    @GET
    @RolesAllowed({"ADMIN", "STAFF"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions(
            @Context UriInfo uriInfo,
            @QueryParam("pageNo") @DefaultValue("1") Integer pageNo,
            @QueryParam("pageSize") @DefaultValue("10") Integer pageSize,
            @QueryParam("params") String params,
            @QueryParam("sortBy") @DefaultValue("id") String sortBy
    ) {
        // Extract filters from query parameters
        Map<String, String> filters = uriInfo.getQueryParameters()
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().matches("pageNo|pageSize|params|sortBy"))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(0)
                ));

        return Response.ok(
                transactionService.getAllTransactions(PagingRequest.builder()
                        .pageNo(pageNo)
                        .pageSize(pageSize)
                        .params(params)
                        .filters(filters)
                        .sortBy(sortBy)
                        .build())
        ).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "STAFF"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(
            @Valid TransactionCreateRequest request
    ) {
        transactionService.createTransaction(request);
        return Response
                .status(Response.Status.CREATED)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.CREATED.toString())
                                .message("Transaction created successfully")
                                .build()
                )
                .build();
    }

    @PUT
    @Path("/{transactionId}")
    @RolesAllowed({"ADMIN", "STAFF"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTransaction(
            @PathParam("transactionId") Integer transactionId,
            @Valid TransactionUpdateRequest request
    ) {
        transactionService.updateTransaction(transactionId, request);
        return Response
                .status(Response.Status.OK)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.OK.toString())
                                .message("Transaction updated successfully")
                                .build()
                )
                .build();
    }

    @DELETE
    @Path("/{transactionId}")
    @RolesAllowed({"ADMIN", "STAFF"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTransaction(
            @PathParam("transactionId") Integer transactionId
    ) {
        transactionService.deleteTransaction(transactionId);
        return Response
                .status(Response.Status.OK)
                .entity(
                        ObjectResponse.builder()
                                .status(Response.Status.OK.toString())
                                .message("Transaction deleted successfully")
                                .build()
                )
                .build();
    }
}