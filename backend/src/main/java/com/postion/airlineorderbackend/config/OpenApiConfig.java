package com.postion.airlineorderbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "航空订单系统API",
        version = "1.0",
        description = "航空订单系统的后端API文档"
    )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("航空订单系统API")
                .version("1.0")
                .description("提供航班查询、订单管理、用户管理等功能的API文档"));
    }
}