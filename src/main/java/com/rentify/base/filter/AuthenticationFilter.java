package com.rentify.base.filter;

import com.rentify.base.exception.ErrorMessage;
import com.rentify.base.exception.UnauthorizedException;
import com.rentify.base.security.JwtGenerator;
import com.rentify.base.security.JwtPayload;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.SneakyThrows;

import java.io.IOException;

@Provider
@Secure
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String JWT_PAYLOAD_ATTRIBUTE = "jwtPayload";

    @Inject
    private JwtGenerator jwtGenerator;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        String token = getTokenFromHeader(reqCtx);
        JwtPayload payload = getPayloadFromToken(token);
        reqCtx.setProperty(JWT_PAYLOAD_ATTRIBUTE, payload);
    }

    @SneakyThrows
    private JwtPayload getPayloadFromToken(String token) {
        try {
            return JwtPayload.fromMap(jwtGenerator.validateToken(token));
        } catch (UnauthorizedException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    private String getTokenFromHeader(ContainerRequestContext reqCtx) {
        String authHeader = reqCtx.getHeaderString(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(ErrorMessage.MISSING_INVALID_HEADER);
        }

        String[] parts = authHeader.split(" ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new UnauthorizedException(ErrorMessage.MISSING_TOKEN);
        }

        return parts[1].trim();
    }

}
