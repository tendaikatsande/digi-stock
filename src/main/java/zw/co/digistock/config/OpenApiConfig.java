package zw.co.digistock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for DigiStock API documentation
 *
 * Access the Swagger UI at: /swagger-ui.html
 * Access the OpenAPI spec at: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI digistockOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DigiStock API")
                .description("""
                    Digital Livestock Management System for Zimbabwe

                    A comprehensive platform to:
                    - Replace paper livestock cards with digital records
                    - Enable real-time verification of cattle ownership and movement
                    - Combat cattle rustling through biometric identification
                    - Streamline movement permits and police clearances
                    - Provide traceability from birth to slaughter

                    ## Pagination
                    All list endpoints support pagination with the following query parameters:
                    - `page`: Page number (0-indexed, default: 0)
                    - `size`: Page size (default: 20, max: 100)
                    - `sortBy`: Field to sort by (default: createdAt)
                    - `sortDir`: Sort direction ASC/DESC (default: DESC)

                    Example: `/api/v1/owners?page=0&size=20&sortBy=lastName&sortDir=ASC`

                    ## Authentication
                    The API uses JWT Bearer tokens for authentication.
                    Include the token in the Authorization header: `Authorization: Bearer <token>`

                    Note: Some endpoints temporarily use `X-Officer-Id` header during development.
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("DigiStock Support")
                    .email("support@digistock.zw")
                    .url("https://digistock.zw"))
                .license(new License()
                    .name("Copyright © 2025 Zimbabwe Government")
                    .url("https://digistock.zw/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server"),
                new Server()
                    .url("https://api.digistock.zw")
                    .description("Production server")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication. Format: Bearer {token}")));
    }
}
