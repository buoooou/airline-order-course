package com.postion.airlineorderbackend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("航空公司订单管理系统").description("航空公司订单管理系统的RESTful").version("1.0.0")
        .contact(new Contact().name("sample contact name").email("sample@support.com").url("https://github.com/xxx")))
        .servers(Arrays.asList(
            new Server().url("http://localhost:8080").description("开发环境"),
            new Server().url("https://aabbcc.com").description("生产环境")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components().addSecuritySchemes("bearerAuth",
            new SecurityScheme().name("bearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .description("JWT认证令牌")));
  }
}
