package com.rentify.transaction.rest;

import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.filter.Secure;
import com.rentify.base.response.ResponseBody;
import com.rentify.transaction.dto.TransactionRequestDTO;
import com.rentify.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("transactions")
@Tag(name = "Transaction", description = "Operations related to transaction")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class TransactionRest {

    @Inject
    private TransactionService transactionService;

    @GET
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get transactions successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get All Transactions", description = "Retrieve a list of all transactions")
    public Response getAllTransactions() {
        return Response.ok().entity(new ResponseBody<>(
                "Get transactions successfully",
                transactionService.findAll()
        )).build();
    }

    @GET
    @Path("/{transactionId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a transaction", description = "Get a transaction by their ID")
    public Response getTransactionById(@PathParam("transactionId") Long transactionId) {
        return Response.ok().entity(new ResponseBody<>("Transaction retrieved successfully", transactionService.findById(transactionId))).build();
    }

    @PUT
    @Path("/{transactionId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Update a transaction", description = "Update a transaction by their ID")
    public Response updateTransaction(@PathParam("transactionId") Long transactionId, TransactionRequestDTO transactionUpdateDTO) {
        return Response.ok().entity(new ResponseBody<>("Transaction updated successfully", transactionService.updateTransaction(transactionId, transactionUpdateDTO))).build();
    }

    @DELETE
    @Path("/{transactionId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Delete a transaction", description = "Deletes a transaction by their ID")
    public Response deleteTransaction(@PathParam("transactionId") Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return Response.noContent().entity(new ResponseBody<>("Transaction deleted successfully", null)).build();
    }


}
