package com.postion.airlineorderbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * 
 * <p>
 * 配置OpenAPI文档和JWT认证功能，支持在Swagger UI中进行接口测试
 * </p>
 * 
 * <p>
 * 功能包括：
 * </p>
 * <ul>
 *     <li>API文档基本信息配置</li>
 *     <li>JWT Bearer Token认证配置</li>
 *     <li>接口安全要求设置</li>
 * </ul>
 * 
 * @author 朱志群
 * @version 1.0
 * @since 2024-07-26
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI实例
     * 
     * <p>
     * 配置API文档基本信息和JWT认证方案，支持在Swagger UI中添加认证令牌
     * </p>
     * 
     * @return OpenAPI配置实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // 定义安全方案名称
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("输入JWT令牌，格式：Bearer {token}")))
                .info(new Info()
                        .title("航空订单管理系统API")
                        .description("航空订单管理系统的RESTful API文档，包含用户认证、订单管理等接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("系统管理员")
                                .email("admin@test.com")
                                .url("https://www.zhuzhiqunlearning.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}