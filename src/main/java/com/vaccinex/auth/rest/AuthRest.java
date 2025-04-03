//package com.vaccinex.auth.rest;
//
//import com.vaccinex.auth.contants.AuthResponseMessage;
//import com.vaccinex.auth.dto.LoginRequestDTO;
//import com.vaccinex.auth.dto.RegisterRequestDTO;
//import com.vaccinex.auth.service.AuthService;
//import com.vaccinex.base.contants.ApplicationMessage;
//import com.vaccinex.base.response.ResponseBody;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.annotation.security.PermitAll;
//import jakarta.inject.Inject;
//import jakarta.validation.Valid;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//@Path("auth")
//@Tag(name = "Authentication", description = "Operations related to authentication")
//@SecurityScheme(
//        name = "bearerAuth",
//        type = SecuritySchemeType.HTTP,
//        scheme = "Bearer",
//        bearerFormat = "JWT"
//)
//public class AuthRest {
//
//    @Inject
//    private AuthService authService;
//
//    @POST
//    @Path("login")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_JSON})
//    @PermitAll
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Login successfully"),
//            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
//            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
//    })
//    public Response login(@Valid LoginRequestDTO loginDTO) {
//        return Response.ok().entity(new ResponseBody<>(
//                AuthResponseMessage.LOGIN_SUCCESSFULLY,
//                authService.login(loginDTO)
//        )).build();
//    }
//
//    @POST
//    @Path("register")
//    @Consumes({MediaType.APPLICATION_JSON})
//    @PermitAll
//    @Produces({MediaType.APPLICATION_JSON})
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "Register successfully"),
//            @ApiResponse(responseCode = "400", description = ApplicationMessage.BAD_REQUEST_ERROR),
//            @ApiResponse(responseCode = "500", description = ApplicationMessage.INTERNAL_SEVER_ERROR)
//    })
//    public Response register(RegisterRequestDTO requestDTO) {
//        return Response.status(Response.Status.CREATED).entity(new ResponseBody<>(
//                AuthResponseMessage.REGISTER_SUCCESSFULLY,
//                authService.register(requestDTO)
//        )).build();
//    }
//
//
//}
