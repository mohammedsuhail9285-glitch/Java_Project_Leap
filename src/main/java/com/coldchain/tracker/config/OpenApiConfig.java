package com.coldchain.tracker.config;

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
 * Swagger / OpenAPI 3.0 configuration with JWT Bearer authentication scheme.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI coldChainOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Automated Cold-Chain Logistics & Temperature Compliance Tracker")
                        .description("Production-grade backend logistics engine for tracking temperature-sensitive " +
                                "pharmaceuticals, vaccines, and perishables. Features automated IoT telemetry ingestion, " +
                                "real-time rule violation evaluation (Safe Range: 2°C to 8°C), and instant alert dispatch.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cold-Chain Logistics Engineering Team")
                                .email("support@coldchain.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token obtained from /api/v1/auth/login or /api/v1/users/login")));
    }
}
