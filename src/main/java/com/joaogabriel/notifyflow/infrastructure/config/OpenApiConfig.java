package com.joaogabriel.notifyflow.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger documentation configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notifyFlowOpenApi(@Value("${app.base-url:}") String baseUrl) {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("NotifyFlow API")
                        .description("Multi-channel async notification engine with delivery guarantee")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("João Gabriel")
                        )
                );

        if (baseUrl != null && !baseUrl.isBlank()) {
            openAPI.addServersItem(new Server().url(baseUrl).description("Production server"));
        }

        return openAPI;
    }
}
