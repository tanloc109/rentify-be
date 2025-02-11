package com.rentify.type.rest;

import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.filter.Secure;
import com.rentify.base.response.ResponseBody;
import com.rentify.type.dto.TypeRequestDTO;
import com.rentify.type.service.TypeService;
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

@Path("types")
@Tag(name = "Type", description = "Operations related to type")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class TypeRest {

    @Inject
    private TypeService typeService;

    @GET
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get types successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get All Types", description = "Retrieve a list of all types")
    public Response getAllTypes() {
        return Response.ok().entity(new ResponseBody<>(
                "Get types successfully",
                typeService.findAll()
        )).build();
    }

    @GET
    @Path("/{typeId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Type not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a type", description = "Get a type by their ID")
    public Response getTypeById(@PathParam("typeId") Long typeId) {
        return Response.ok().entity(new ResponseBody<>("Type retrieved successfully", typeService.findById(typeId))).build();
    }

    @POST
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Type created successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Create a type", description = "Create a new type")
    public Response createType(TypeRequestDTO typeRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Type created successfully", typeService.createType(typeRequestDTO))).build();
    }


    @PUT
    @Path("/{typeId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type updated successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Type not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Update a type", description = "Update a type by their ID")
    public Response updateType(@PathParam("typeId") Long typeId, TypeRequestDTO typeUpdateDTO) {
        return Response.ok().entity(new ResponseBody<>("Type updated successfully", typeService.updateType(typeId, typeUpdateDTO))).build();
    }

    @DELETE
    @Path("/{typeId}")
    @Secure(roles = "ADMIN")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Type deleted successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Type not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Delete a type", description = "Deletes a type by their ID")
    public Response deleteType(@PathParam("typeId") Long typeId) {
        typeService.deleteType(typeId);
        return Response.noContent().entity(new ResponseBody<>("Type deleted successfully", null)).build();
    }


}
