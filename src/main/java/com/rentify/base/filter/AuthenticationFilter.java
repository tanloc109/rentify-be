package com.rentify.base.filter;

import com.rentify.base.contants.ApplicationMessage;
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
    private JwtPayload getPayloadFromToken(String token) {
        try {
            return JwtPayload.fromMap(jwtGenerator.validateToken(token));
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
        }
    }

    private String getTokenFromHeader(ContainerRequestContext reqCtx) {
        String authHeader = reqCtx.getHeaderString(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(ApplicationMessage.MISSING_TOKEN_ERROR);
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
        }

        return token;
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
