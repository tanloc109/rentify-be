package com.rentify.base.filter;

import com.rentify.base.exception.ForbiddenException;
import com.rentify.base.exception.UnauthorizedException;
import com.rentify.base.security.JwtGenerator;
import com.rentify.base.security.JwtPayload;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Secure
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String JWT_PAYLOAD_ATTRIBUTE = "jwtPayload";

    @Inject
    private JwtGenerator jwtGenerator;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        String token = getTokenFromHeader(reqCtx);
        JwtPayload payload = getPayloadFromToken(token);
        reqCtx.setProperty(JWT_PAYLOAD_ATTRIBUTE, payload);

        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(Secure.class)) {
            Secure secure = method.getAnnotation(Secure.class);
            checkAccess(String.valueOf(payload.getRole()), Arrays.asList(secure.roles()));
        }
    }

    // Throws thử + ý nghĩa
    @SneakyThrows
    private JwtPayload getPayloadFromToken(String token) {
        try {
            return JwtPayload.fromMap(jwtGenerator.validateToken(token));
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    private String getTokenFromHeader(ContainerRequestContext reqCtx) {
        String authHeader = reqCtx.getHeaderString(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header");
        }

        String[] parts = authHeader.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new UnauthorizedException("Invalid or expired token!");
        }

        return parts[1].trim();
    }

    private void checkAccess(String userRole, List<String> allowedRoles) {
        if (!allowedRoles.contains(userRole)) {
            throw new ForbiddenException("Access denied: insufficient permissions");
        }
    }
}
