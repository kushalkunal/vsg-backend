package com.vsg.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI vsgOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Visionary Salva Group API")
                .description("Multi-tenant consultancy backend — pass `x-tenant-id` header and Bearer JWT on all protected endpoints.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("VSG Engineering")
                    .email("admin@vsg.com"))
                .license(new License()
                    .name("Proprietary")))
            .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
            .components(new Components()
                .addSecuritySchemes(BEARER_SCHEME,
                    new SecurityScheme()
                        .name(BEARER_SCHEME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Enter your JWT token obtained from POST /auth/login")));
    }
}
