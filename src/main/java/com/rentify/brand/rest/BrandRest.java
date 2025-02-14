package com.rentify.brand.rest;

import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.response.ResponseBody;
import com.rentify.brand.dto.BrandRequestDTO;
import com.rentify.brand.service.BrandService;
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

@Path("brands")
@Tag(name = "Brand", description = "Operations related to brand")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class BrandRest {

    @Inject
    private BrandService brandService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get brands successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get All Brands", description = "Retrieve a list of all brands")
    public Response getAllBrands() {
        return Response.ok().entity(new ResponseBody<>(
                "Get brands successfully",
                brandService.findAll()
        )).build();
    }

    @GET
    @Path("/{brandId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Brand not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a brand", description = "Get a brand by their ID")
    public Response getBrandById(@PathParam("brandId") Long brandId) {
        return Response.ok().entity(new ResponseBody<>("Brand retrieved successfully", brandService.findById(brandId))).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Brand created successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Create a brand", description = "Create a new brand")
    public Response createBrand(BrandRequestDTO brandRequestDTO) {
        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>("Brand created successfully", brandService.createBrand(brandRequestDTO))).build();
    }


    @PUT
    @Path("/{brandId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Brand not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Update a brand", description = "Update a brand by their ID")
    public Response updateBrand(@PathParam("brandId") Long brandId, BrandRequestDTO brandUpdateDTO) {
        return Response.ok().entity(new ResponseBody<>("Brand updated successfully", brandService.updateBrand(brandId, brandUpdateDTO))).build();
    }

    @DELETE
    @Path("/{brandId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "Brand not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Delete a brand", description = "Deletes a brand by their ID")
    public Response deleteBrand(@PathParam("brandId") Long brandId) {
        brandService.deleteBrand(brandId);
        return Response.noContent().entity(new ResponseBody<>("Brand deleted successfully", null)).build();
    }


}
