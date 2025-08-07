package com.postion.airlineorderbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Airline Order System")
                        .description("Airline Order System RESTful")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Postion")
                                .email("support@postion.app")
                                .url("https://github.com/buoooou"))
                )
                .servers(java.util.Arrays.asList(
                        new Server().url("http://localhost:8080").description("Dev Envoriment"),
                        new Server().url("https://api.airline.com").description("Prod Envoriment")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Token")));
    }
}