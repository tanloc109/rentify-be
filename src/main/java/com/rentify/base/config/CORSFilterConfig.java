package com.rentify.base.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebListener
public class CORSFilterConfig implements ServletContextListener {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            AppConfig.getAllowedOrigin().split(",")
    );

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.addFilter("CORSFilter", new CORSFilter()).addMappingForUrlPatterns(null, false, "/*");
    }

    private static class CORSFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            setCorsHeaders(httpRequest, httpResponse);

            if (isPreflightRequest(httpRequest)) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            chain.doFilter(request, response);
        }

        private void setCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
            String origin = request.getHeader("Origin");
            if (ALLOWED_ORIGINS.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            }
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        private boolean isPreflightRequest(HttpServletRequest request) {
            return "OPTIONS".equalsIgnoreCase(request.getMethod());
        }
    }
}
