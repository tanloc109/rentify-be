//package com.rentify.base.filter;
//
//import com.rentify.base.contants.ApplicationMessage;
//import com.rentify.base.exception.UnauthorizedException;
//import com.rentify.base.security.JwtGenerator;
//import com.rentify.base.security.JwtPayload;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
//import jakarta.security.enterprise.AuthenticationStatus;
//import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.ws.rs.core.HttpHeaders;
//import java.util.Set;
//
//@ApplicationScoped
//public class CustomAuthMechanism implements HttpAuthenticationMechanism {
//
//    private static final String BEARER_PREFIX = "Bearer ";
//
//    @Inject
//    private JwtGenerator jwtGenerator;
//
//    @Override
//    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {
//        String requestUri = request.getRequestURI();
//
//        // ✅ Bỏ qua authentication nếu request vào Swagger hoặc các API public
//        if (requestUri.contains("/openapi") ||
//                requestUri.contains("/swagger")||
//                requestUri.contains("/api-docs")
//        ) {
//            return context.doNothing();
//        }
//
//        String token = getTokenFromHeader(request);
//        if (token == null) {
//            return context.responseUnauthorized();
//        }
//
//        JwtPayload payload = getPayloadFromToken(token);
//
//        // ✅ Lấy role dưới dạng chuỗi
//        String userRole = payload.getRole().getValue();
//
//        System.out.println(userRole);
//
//        // ✅ Đảm bảo role truyền vào đúng format Jakarta Security yêu cầu
//        return context.notifyContainerAboutLogin(payload.getEmail(), Set.of(userRole));
//    }
//
//
//
//    private String getTokenFromHeader(HttpServletRequest request) {
//        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
//            return null; // Không có token, Jakarta Security xử lý tiếp
//        }
//        return authHeader.substring(BEARER_PREFIX.length()).trim();
//    }
//
//    private JwtPayload getPayloadFromToken(String token) {
//        try {
//            return JwtPayload.fromMap(jwtGenerator.validateToken(token));
//        } catch (UnauthorizedException e) {
//            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
//        }
//    }
//}
