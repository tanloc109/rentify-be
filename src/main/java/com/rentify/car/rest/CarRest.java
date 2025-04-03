package com.rentify.car.rest;

import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.response.ResponseBody;
import com.rentify.car.dto.CarRequestDTO;
import com.rentify.car.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("cars")
@Tag(name = "Car", description = "Operations related to car")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class CarRest {

    @Inject
    private CarService carService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get cars successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get All Cars", description = "Retrieve a list of all cars")
    public Response getAllCars() {
        return Response.ok().entity(new ResponseBody<>(
                "Get cars successfully",
                carService.findAll()
        )).build();
    }

    @GET
    @Path("/{carId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @RolesAllowed("RENTER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a car", description = "Get a car by their ID")
    public Response getCarById(@PathParam("carId") Long carId) {
        return Response.ok().entity(new ResponseBody<>("Car retrieved successfully", carService.findById(carId))).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Car created successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Create a car", description = "Create a new car")
    public Response createCar(CarRequestDTO carRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Car created successfully", carService.createCar(carRequestDTO))).build();
    }


    @PUT
    @Path("/{carId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car updated successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Update a car", description = "Update a car by their ID")
    public Response updateCar(@PathParam("carId") Long carId, CarRequestDTO carUpdateDTO) {
        return Response.ok().entity(new ResponseBody<>("Car updated successfully", carService.updateCar(carId, carUpdateDTO))).build();
    }

    @DELETE
    @Path("/{carId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Delete a car", description = "Deletes a car by their ID")
    public Response deleteCar(@PathParam("carId") Long carId) {
        carService.deleteCar(carId);
        return Response.noContent().entity(new ResponseBody<>("Car deleted successfully", null)).build();
    }


}
