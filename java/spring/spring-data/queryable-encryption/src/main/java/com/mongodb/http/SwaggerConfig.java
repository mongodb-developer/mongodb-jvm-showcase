package com.mongodb.http;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Queryable Encryption with Spring Data API")
                        .version("1.0")
                        .description("API for managing encrypted employees in MongoDB"));
    }

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group("employees")
                .pathsToMatch("/employees/**")
                .build();
    }
}
