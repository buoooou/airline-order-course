package com.postion.airlineorderbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置类
 * 配置API文档的基本信息和JWT认证支持
 */
@Configuration
public class OpenApiConfig {

    /**
     * 自定义OpenAPI配置
     * 配置API基本信息和JWT Bearer认证
     * 
     * @return 自定义的OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("航空订单管理系统 API")
                        .version("1.0.0")
                        .description("航空订单管理系统的RESTful API文档，提供订单创建、支付、取消、出票等功能"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT认证令牌，格式: Bearer {token}")
                        )
                );
    }
}