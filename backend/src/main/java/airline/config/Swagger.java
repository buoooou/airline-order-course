package airline.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {
    @Bean
    public OpenAPI awsAgenticAiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AWS Agentic AI Recruitment System API")
                        .description("API documentation for AWS Agentic AI Recruitment System")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}