package com.vaccinex;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;

@ApplicationScoped
@ApplicationPath("/api/v1")
@OpenAPIDefinition(
        info = @Info(title = "Vaccine X API", version = "0.1.0", description = "API Documentation"),
        servers = @Server(url = "http://localhost:8080/vaccineX/")

)
public class VaccineX extends Application {

}
