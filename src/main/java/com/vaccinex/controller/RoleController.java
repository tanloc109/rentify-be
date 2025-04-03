package com.vaccinex.controller;

import com.vaccinex.dto.request.RoleRequestDTO;
import com.vaccinex.dto.response.RoleResponseDTO;

import com.vaccinex.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@Path("/roles")
@Tag(name = "Role", description = "Role Management Operations")
public class RoleController {
    private static final Logger LOGGER = Logger.getLogger(RoleController.class.getName());

    @Inject
    private RoleService roleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRoles() {
        List<RoleResponseDTO> roles = roleService.findAll();
        return Response.ok(roles).build();
    }

    @GET
    @Path("/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoleById(@PathParam("roleId") Integer roleId) {
        RoleResponseDTO role = roleService.findById(roleId);
        return Response.ok(role).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewRole(@Valid RoleRequestDTO dto) throws Exception {
        RoleResponseDTO createdRole = roleService.createRole(dto);
        return Response.status(Response.Status.CREATED).entity(createdRole).build();
    }

    @PUT
    @Path("/{roleId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRole(
            @PathParam("roleId") Integer roleId,
            @Valid RoleRequestDTO dto
    ) {
        RoleResponseDTO updatedRole = roleService.update(roleId, dto);
        return Response.ok(updatedRole).build();
    }

    @DELETE
    @Path("/{roleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoleById(@PathParam("roleId") Integer roleId) {
        roleService.deleteById(roleId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}