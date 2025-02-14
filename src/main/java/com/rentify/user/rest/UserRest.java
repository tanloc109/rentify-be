package com.rentify.user.rest;

import com.rentify.auth.contants.AuthResponseMessage;
import com.rentify.base.contants.ApplicationMessage;
import com.rentify.base.response.ResponseBody;
import com.rentify.user.dto.UserDTO;
import com.rentify.user.dto.UserUpdateRequest;
import com.rentify.user.service.UserService;
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

@Path("users")
@Tag(name = "User", description = "Operations related to user")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT"
)
public class UserRest {

    @Inject
    private UserService userService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Get users successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get All Users", description = "Retrieve a list of all users")
    public Response getALlUser() {
        return Response.ok().entity(new ResponseBody<>(
                AuthResponseMessage.LOGIN_SUCCESSFULLY,
                userService.findAll()
        )).build();
    }

    @GET
    @Path("/{userId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a user", description = "Get a user by their ID")
    public Response getUserById(@PathParam("userId") Long userId) {
        UserDTO user = userService.findById(userId);
        return Response.ok().entity(new ResponseBody<>("User retrieved successfully", user)).build();
    }

    @GET
    @Path("/email")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Get a user", description = "Get a user by their email")
    public Response getUserByEmail(@QueryParam("email") String email) {
        UserDTO user = userService.findByEmail(email);
        return Response.ok().entity(new ResponseBody<>("User retrieved successfully", user)).build();
    }

    @PUT
    @Path("/{userId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Update a user", description = "Update a user by their ID")
    public Response updateUser(@PathParam("userId") Long userId, UserUpdateRequest userDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return Response.ok().entity(new ResponseBody<>("User updated successfully", updatedUser)).build();
    }

    @DELETE
    @Path("/{userId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
            @ApiResponse(responseCode = "401", description = ApplicationMessage.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = ApplicationMessage.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
    })
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID")
    public Response deleteUser(@PathParam("userId") Long userId) {
        userService.deleteUser(userId);
        return Response.noContent().entity(new ResponseBody<>("User deleted successfully", null)).build();
    }

}
