package com.vaccinex.controller;

import com.vaccinex.dto.request.OrderRequest;

import com.vaccinex.dto.response.ObjectResponse;
import com.vaccinex.service.OrderService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
@Tag(name = "Order", description = "Order Management Operations")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class OrderController {

    @Inject
    private OrderService orderService;

    @POST
    @Path("/deposit")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(
            @Context HttpServletRequest http,
            @Valid OrderRequest request
    ) {
        Object orderResult = orderService.createOrder(request, http);
        return Response.status(Response.Status.CREATED).entity(orderResult).build();
    }

    @POST
    @Path("/{orderId}/cancel")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(@PathParam("orderId") Integer orderId) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Order canceled successfully")
                        .data(orderService.refundOrder(orderId))
                        .build()
        ).build();
    }

    @GET
    @Path("/{orderId}/refund/amount")
    @RolesAllowed("USER")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateRefundAmount(@PathParam("orderId") Integer orderId) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Refund amount calculated")
                        .data(orderService.calculateRefundAmount(orderId))
                        .build()
        ).build();
    }

    @GET
    @Path("/history/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "bearerAuth")
    public Response getOrderHistory(@PathParam("customerId") Integer customerId) {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Order history retrieved successfully")
                        .data(orderService.getOrdersWithSchedulesByCustomerId(customerId))
                        .build()
        ).build();
    }

    @GET
    @Path("/summary")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderSummary() {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Order summary retrieved successfully")
                        .data(orderService.getOrderSummary())
                        .build()
        ).build();
    }

    @GET
    @Path("/revenue")
    @SecurityRequirement(name = "bearerAuth")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRevenue() {
        return Response.ok(
                ObjectResponse.builder()
                        .status(Response.Status.OK.toString())
                        .message("Revenue retrieved successfully")
                        .data(orderService.getRevenue())
                        .build()
        ).build();
    }
}