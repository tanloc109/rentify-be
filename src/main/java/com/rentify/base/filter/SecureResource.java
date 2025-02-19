//package com.rentify.base.filter;
//
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import jakarta.annotation.security.PermitAll;
//import jakarta.annotation.security.RolesAllowed;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//
//@Path("/secure")
//@SecurityScheme(
//        name = "bearerAuth",
//        type = SecuritySchemeType.HTTP,
//        scheme = "Bearer",
//        bearerFormat = "JWT"
//)
//public class SecureResource {
//
//    @GET
//    @Path("/admin")
//    @Produces(MediaType.TEXT_PLAIN)
//    @RolesAllowed("ADMIN")
//    @SecurityRequirement(name = "bearerAuth")
//    public String adminAccess() {
//        return "Chỉ ADMIN mới truy cập được!";
//    }
//
//    @GET
//    @Path("/user")
//    @Produces(MediaType.TEXT_PLAIN)
//    @RolesAllowed("RENTER")
//    @SecurityRequirement(name = "bearerAuth")
//    public String userAccess() {
//        return "Chỉ USER mới truy cập được!";
//    }
//
//    // ✅ 1️⃣ Endpoint không cần token, ai cũng truy cập được
//    @GET
//    @Path("/public")
//    @Produces(MediaType.TEXT_PLAIN)
//    @PermitAll
//    public String publicAccess() {
//        return "Bất kỳ ai cũng truy cập được!";
//    }
//
//    // ✅ 2️⃣ Endpoint cho cả ADMIN & USER
//    @GET
//    @Path("/common")
//    @Produces(MediaType.TEXT_PLAIN)
//    @RolesAllowed({"ADMIN", "RENTER"})
//    @SecurityRequirement(name = "bearerAuth")
//    public String commonAccess() {
//        return "ADMIN và USER đều truy cập được!";
//    }
//}
