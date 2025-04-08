package com.vaccinex.controller;

import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;
import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.dto.response.TransactionResponse;
import com.vaccinex.service.TransactionService;
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

import java.util.List;

@Path("/transactions")
@Tag(name = "Transactions", description = "Transaction Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    @Inject
    private TransactionService transactionService;

    @GET
    @RolesAllowed({"ADMIN", "STAFF"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return Response.ok(transactions).build();
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