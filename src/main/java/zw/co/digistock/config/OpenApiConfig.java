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

                    ## Authentication Flow

                    ### 1. Register a New Officer (Optional)
                    **POST** `/api/v1/auth/register`

                    Create a new officer account with email, password, and role (ADMIN, AGRITEX_OFFICER, POLICE_OFFICER, etc.)

                    **Example Request:**
                    ```json
                    {
                      "officerCode": "AG-001",
                      "firstName": "John",
                      "lastName": "Doe",
                      "email": "john.doe@agritex.gov.zw",
                      "phoneNumber": "+263771234567",
                      "password": "SecurePass123!",
                      "role": "AGRITEX_OFFICER",
                      "province": "Harare",
                      "district": "Harare"
                    }
                    ```

                    ### 2. Login
                    **POST** `/api/v1/auth/login`

                    Authenticate with email and password to receive a JWT token.

                    **Example Request:**
                    ```json
                    {
                      "email": "admin@digistock.zw",
                      "password": "Admin@123"
                    }
                    ```

                    **Example Response:**
                    ```json
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "tokenType": "Bearer",
                      "expiresIn": 86400000,
                      "officerId": "123e4567-e89b-12d3-a456-426614174000",
                      "email": "admin@digistock.zw",
                      "fullName": "System Administrator",
                      "officerCode": "ADMIN-001",
                      "role": "ADMIN",
                      "active": true
                    }
                    ```

                    ### 3. Use the Token
                    Include the JWT token in the **Authorization** header for all protected endpoints:

                    ```
                    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                    ```

                    Click the **Authorize** button (🔓) at the top of this page to set your token for all requests.

                    ### 4. Default Test Credentials
                    For testing, use the pre-configured admin account:
                    - **Email:** admin@digistock.zw
                    - **Password:** Admin@123

                    ## Password Management

                    ### Forgot Password
                    **POST** `/api/v1/auth/forgot-password`

                    If you forgot your password, request a password reset:
                    1. Provide your email address
                    2. Receive a reset token (in production, sent via email)
                    3. Token is valid for 1 hour

                    **Example Request:**
                    ```json
                    {
                      "email": "john.doe@agritex.gov.zw"
                    }
                    ```

                    ### Reset Password
                    **POST** `/api/v1/auth/reset-password`

                    Use the reset token to set a new password:

                    **Example Request:**
                    ```json
                    {
                      "token": "abc123xyz789",
                      "newPassword": "NewSecure123!",
                      "confirmPassword": "NewSecure123!"
                    }
                    ```

                    ### Change Password (Authenticated)
                    **POST** `/api/v1/auth/change-password`

                    Change your password when already logged in. Requires authentication.

                    **Example Request:**
                    ```json
                    {
                      "currentPassword": "OldPassword123!",
                      "newPassword": "NewSecure123!",
                      "confirmPassword": "NewSecure123!"
                    }
                    ```

                    **Password Requirements:**
                    - Minimum 8 characters
                    - At least one uppercase letter
                    - At least one lowercase letter
                    - At least one digit
                    - At least one special character (@$!%*?&)

                    ## Pagination
                    All list endpoints support pagination with the following query parameters:
                    - `page`: Page number (0-indexed, default: 0)
                    - `size`: Page size (default: 20, max: 100)
                    - `sortBy`: Field to sort by (default: createdAt)
                    - `sortDir`: Sort direction ASC/DESC (default: DESC)

                    Example: `/api/v1/owners?page=0&size=20&sortBy=lastName&sortDir=ASC`
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
