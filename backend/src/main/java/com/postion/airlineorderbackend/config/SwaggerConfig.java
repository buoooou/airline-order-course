package com.postion.airlineorderbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
/**
 * Swagger3配置类
 * 访问地址: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 配置API文档元信息
        .info(new Info().title("航空公司后台管理系统API文档")
               .description("本文档描述了航空公司后台管理系统的API接口")
               .version("1.0.0")
               .contact(new Contact()
                       .name("ycr")
                       .email("cryue@cn.ibm.com")
                       .url("https://github.com/YUECAIRONG")))
        .servers(java.util.Arrays.asList(
            new Server().url("http://localhost:8080/").description("本地测试环境"),
            new Server().url("http://api.airline.com/").description("生产环境")
            ))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
