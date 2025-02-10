package com.rentify;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("api")
@OpenAPIDefinition(
        info = @Info(title = "Rentify API", version = "0.1.0", description = "API Documentation"),
        servers = @Server(url = "http://localhost:8080/rentify/")

)
public class Rentify extends Application {
}
