package com.ramiyacoub.hrmanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hrManagementApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HR Management API")
                        .description(
                                "REST API for managing employees, leaves and expense claims."
                        )
                        .version("1.0")
                        .contact(new Contact()
                                .name("Rami Yacoub")));
    }
}