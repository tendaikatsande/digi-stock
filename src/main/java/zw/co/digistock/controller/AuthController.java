package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.dto.request.LoginRequest;
import zw.co.digistock.dto.request.RegisterOfficerRequest;
import zw.co.digistock.dto.response.AuthResponse;
import zw.co.digistock.service.AuthService;

/**
 * Controller for authentication endpoints
 * These endpoints do NOT require authentication (public endpoints)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication",
    description = """
        Officer authentication and registration endpoints.

        **Note:** These endpoints are public and do not require authentication.
        Use these endpoints to obtain a JWT token for accessing protected resources.
        """
)
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate officer and get JWT token
     */
    @PostMapping("/login")
    @Operation(
        summary = "Officer login",
        description = """
            Authenticate an officer with email and password to receive a JWT token.

            **Authentication Flow:**
            1. Send email and password in request body
            2. Receive JWT token in response
            3. Use token in Authorization header: `Bearer {token}`

            **Default test credentials:**
            - Email: admin@digistock.zw
            - Password: Admin@123

            **Note:** This endpoint does not require authentication.
            """,
        security = {} // Exclude from global security requirement
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated - Returns JWT token and officer details",
            content = @Content(
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful login",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBkaWdpc3RvY2suenciLCJpYXQiOjE2NzA0MzI0MDAsImV4cCI6MTY3MDUxODgwMH0.abc123",
                          "tokenType": "Bearer",
                          "expiresIn": 86400000,
                          "officerId": "123e4567-e89b-12d3-a456-426614174000",
                          "email": "admin@digistock.zw",
                          "fullName": "System Administrator",
                          "officerCode": "ADMIN-001",
                          "role": "ADMIN",
                          "province": "Harare",
                          "district": "Harare",
                          "active": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials - Email or password is incorrect",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Invalid email or password",
                          "path": "/api/v1/auth/login"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Invalid request format",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Validation failed",
                          "errors": {
                            "email": "Email must be valid",
                            "password": "Password is required"
                          }
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/v1/auth/login - Login request for: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Register a new officer
     */
    @PostMapping("/register")
    @Operation(
        summary = "Register new officer",
        description = """
            Create a new officer account with email, password, and role.

            **Supported Roles:**
            - ADMIN: Full system access
            - AGRITEX_OFFICER: Issue movement permits
            - POLICE_OFFICER: Issue clearances and verify at checkpoints
            - VETERINARY_INSPECTOR: Veterinary inspections

            **Password Requirements:**
            - Minimum 8 characters
            - At least one uppercase letter
            - At least one lowercase letter
            - At least one digit
            - At least one special character (@$!%*?&)

            **Note:** This endpoint does not require authentication.
            """,
        security = {} // Exclude from global security requirement
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully registered - Returns JWT token and officer details",
            content = @Content(
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful registration",
                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBhZ3JpdGV4Lmdvdi56dyIsImlhdCI6MTY3MDQzMjQwMCwiZXhwIjoxNjcwNTE4ODAwfQ.xyz789",
                          "tokenType": "Bearer",
                          "expiresIn": 86400000,
                          "officerId": "987e6543-e21b-12d3-a456-426614174111",
                          "email": "john.doe@agritex.gov.zw",
                          "fullName": "John Doe",
                          "officerCode": "AG-001",
                          "role": "AGRITEX_OFFICER",
                          "province": "Harare",
                          "district": "Harare",
                          "active": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - Email or officer code already exists",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 409,
                          "error": "Conflict",
                          "message": "Officer with email john.doe@agritex.gov.zw already exists",
                          "path": "/api/v1/auth/register"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Invalid request format",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Validation failed",
                          "errors": {
                            "email": "Email must be valid",
                            "password": "Password must be at least 8 characters with uppercase, lowercase, number, and special character",
                            "phoneNumber": "Phone number must be in format +263XXXXXXXXX"
                          }
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterOfficerRequest request) {
        log.info("POST /api/v1/auth/register - Registration request for: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
