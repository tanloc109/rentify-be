package com.vaccinex.base.filter;

import com.vaccinex.base.config.AppConfig;
import com.vaccinex.base.contants.ApplicationMessage;
import com.vaccinex.base.exception.ForbiddenException;
import com.vaccinex.base.exception.UnauthorizedException;
import com.vaccinex.base.security.JwtGenerator;
import com.vaccinex.base.security.JwtPayload;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    // Danh sách các path không cần xác thực
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "auth/login",
            "auth/register",
            "auth/refresh",
            "payment/vn-pay-callback"
    );

    @Inject
    private JwtGenerator jwtGenerator;

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Kiểm tra xem path hiện tại có nằm trong danh sách loại trừ không
        if (isExcludedPath(path)) {
            return;
        }

        Method method = resourceInfo.getResourceMethod();
        System.out.println("method = " + method);
        // Nếu phương thức yêu cầu vai trò cụ thể
        if (method != null && method.isAnnotationPresent(RolesAllowed.class)) {
            String token = getTokenFromHeader(requestContext);
            if (token == null) {
                throw new UnauthorizedException(ApplicationMessage.MISSING_TOKEN_ERROR);
            }

            try {
                // Sử dụng fromToken thay vì fromMap
                JwtPayload jwtPayload = JwtPayload.fromToken(token, AppConfig.getJWTSecretKey());

                if (jwtPayload == null) {
                    throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
                }

                // Kiểm tra quyền truy cập
                RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
                checkAccess(jwtPayload.getRole(), Arrays.asList(rolesAllowed.value()));

                // Thiết lập SecurityContext mới với thông tin người dùng từ token
                requestContext.setSecurityContext(new JwtSecurityContext(jwtPayload, requestContext.getSecurityContext().isSecure()));
            } catch (Exception e) {
                System.out.println("\n\n\ne = " + e);
                throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
            }
        }
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::contains);
    }

    /**
     * Lấy token từ header của ContainerRequestContext
     */
    private String getTokenFromHeader(ContainerRequestContext reqCtx) {
        System.out.println("getTokenFromHeader");
        String authHeader = reqCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * Lấy token từ header của HttpServletRequest - Phương thức này dùng cho services
     */
    public String getTokenFromHeader(HttpServletRequest request) {
        System.out.println("getTokenFromHeader");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    private void checkAccess(String userRole, List<String> allowedRoles) {
        if (allowedRoles.contains("*")) {
            return;
        }
        System.out.println("checkAccess: " + userRole + " - " + allowedRoles);

        // Nếu role có định dạng "ROLE_XXX" thì cắt bỏ "ROLE_" để so sánh
        String roleToCheck = userRole;
        if (userRole != null && userRole.startsWith("ROLE_")) {
            roleToCheck = userRole.substring(5);
        }

        if (!allowedRoles.contains(roleToCheck)) {
            throw new ForbiddenException(ApplicationMessage.UNAUTHORIZED);
        }
    }

    /**
     * SecurityContext để lưu thông tin người dùng được xác thực
     */
    private static class JwtSecurityContext implements SecurityContext {
        private final JwtPayload principal;
        private final boolean secure;

        JwtSecurityContext(JwtPayload principal, boolean secure) {
            this.principal = principal;
            this.secure = secure;
        }

        @Override
        public Principal getUserPrincipal() {
            return principal;
        }

        @Override
        public boolean isUserInRole(String role) {
            String userRole = principal.getRole();
            // Nếu role có định dạng "ROLE_XXX" thì cắt bỏ "ROLE_" để so sánh
            if (userRole != null && userRole.startsWith("ROLE_")) {
                return role.equals(userRole.substring(5));
            }
            return role.equals(userRole);
        }

        @Override
        public boolean isSecure() {
            return secure;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}