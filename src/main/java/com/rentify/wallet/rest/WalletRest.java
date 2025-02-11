package com.rentify.wallet.rest;

import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.filter.Secure;
import com.rentify.base.response.ResponseBody;
import com.rentify.transaction.dto.TransactionRequestDTO;
import com.rentify.wallet.service.WalletService;
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

@Path("wallets")
@Tag(name = "Wallet", description = "Operations related to wallet")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class WalletRest {

    @Inject
    private WalletService walletService;

    @GET
    @Secure(roles = {"ADMIN", "RENTER", "HOST"})
    @Path("/{userId}/balance")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retrieve the balance of a user's wallet successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get Wallet Balance", description = "Retrieve the balance of a user's wallet")
    public Response getBalance(@PathParam("userId") Long userId) {
        return Response.ok().entity(new ResponseBody<>("Retrieve the balance of a user's wallet successfully", walletService.getBalanceByUserId(userId))).build();
    }

    @POST
    @Secure(roles = {"ADMIN", "RENTER", "HOST"})
    @Path("/{userId}/deposit")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deposit money successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Deposit Money", description = "Deposit money into the user's wallet")
    public Response depositMoney(@PathParam("userId") Long userId, TransactionRequestDTO transactionRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Deposit money successfully", walletService.depositMoney(userId, transactionRequestDTO))).build();
    }

    @POST
    @Secure(roles = {"ADMIN", "RENTER", "HOST"})
    @Path("/{userId}/rent-deposit")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Deposit rent money successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Deposit Rent Money", description = "Deposit money for renting a car")
    public Response depositRentMoney(@PathParam("userId") Long userId, TransactionRequestDTO transactionRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Deposit rent money successfully", walletService.depositRentMoney(userId, transactionRequestDTO))).build();
    }

    @POST
    @Secure(roles = {"ADMIN", "RENTER", "HOST"})
    @Path("/{userId}/pay")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment made successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Make Payment", description = "Make a payment using the user's wallet")
    public Response makePayment(@PathParam("userId") Long userId, TransactionRequestDTO transactionRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Payment made successfully", walletService.makePayment(userId, transactionRequestDTO))).build();
    }

    @POST
    @Secure(roles = {"ADMIN", "RENTER", "HOST"})
    @Path("/{userId}/refund")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Refund made successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Refund Money", description = "Refund money to the user's wallet")
    public Response refundMoney(@PathParam("userId") Long userId, TransactionRequestDTO transactionRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Refund made successfully", walletService.refundMoney(userId, transactionRequestDTO))).build();
    }

}