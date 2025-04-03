package com.vaccinex.base.filter;

import com.vaccinex.base.contants.ApplicationMessage;
import com.vaccinex.base.exception.ForbiddenException;
import com.vaccinex.base.exception.UnauthorizedException;
import com.vaccinex.base.security.JwtGenerator;
import com.vaccinex.base.security.JwtPayload;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtGenerator jwtGenerator;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(RolesAllowed.class)) {
            String token = getTokenFromHeader(reqCtx);
            JwtPayload payload = getPayloadFromToken(token);

            RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
            checkAccess(String.valueOf(payload.getRole()), Arrays.asList(rolesAllowed.value()));
        }
    }

    private String getTokenFromHeader(ContainerRequestContext reqCtx) {
        String authHeader = reqCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException(ApplicationMessage.MISSING_TOKEN_ERROR);
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private JwtPayload getPayloadFromToken(String token) {
        try {
            return JwtPayload.fromMap(jwtGenerator.validateToken(token));
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
        }
    }

    private void checkAccess(String userRole, List<String> allowedRoles) {
        if (allowedRoles.contains("*")) {
            return;
        }
        if (!allowedRoles.contains(userRole)) {
            throw new ForbiddenException(ApplicationMessage.UNAUTHORIZED);
        }
    }
}