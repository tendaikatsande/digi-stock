package zw.co.digistock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for DigiStock API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI digistockOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DigiStock API")
                .description("Digital Livestock Management System for Zimbabwe\n\n" +
                    "A comprehensive platform to:\n" +
                    "- Replace paper livestock cards with digital records\n" +
                    "- Enable real-time verification of cattle ownership and movement\n" +
                    "- Combat cattle rustling through biometric identification\n" +
                    "- Streamline movement permits and police clearances\n" +
                    "- Provide traceability from birth to slaughter")
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
            ));
    }
}
