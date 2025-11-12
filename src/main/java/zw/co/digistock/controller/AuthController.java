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
import zw.co.digistock.dto.request.*;
import zw.co.digistock.dto.response.AuthResponse;
import zw.co.digistock.dto.response.MessageResponse;
import zw.co.digistock.service.AuthService;
import zw.co.digistock.security.JwtUtil;

import java.util.UUID;

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
    private final JwtUtil jwtUtil;

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

    /**
     * Forgot password - Request password reset
     */
    @PostMapping("/forgot-password")
    @Operation(
        summary = "Forgot password",
        description = """
            Request a password reset by providing your email address.

            **Password Reset Flow:**
            1. Enter your email address
            2. Receive a reset token (in production, this would be sent via email)
            3. Use the token with the `/reset-password` endpoint to set a new password
            4. Token expires in 1 hour

            **Note:** This endpoint does not require authentication.
            """,
        security = {} // Exclude from global security requirement
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password reset email sent (in production)",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Reset initiated",
                    value = """
                        {
                          "message": "Password reset instructions have been sent to your email. The reset link is valid for 1 hour.",
                          "success": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Email not found",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 404,
                          "error": "Not Found",
                          "message": "No account found with email: user@example.com",
                          "path": "/api/v1/auth/forgot-password"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("POST /api/v1/auth/forgot-password - Password reset requested for: {}", request.getEmail());
        MessageResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password using token
     */
    @PostMapping("/reset-password")
    @Operation(
        summary = "Reset password",
        description = """
            Reset your password using the token received from the forgot password process.

            **Steps:**
            1. Call `/forgot-password` with your email
            2. Receive reset token (via email in production)
            3. Call this endpoint with token and new password
            4. Token must be used within 1 hour

            **Note:** This endpoint does not require authentication.
            """,
        security = {} // Exclude from global security requirement
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password successfully reset",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Reset successful",
                    value = """
                        {
                          "message": "Password has been successfully reset. You can now login with your new password.",
                          "success": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired token, or passwords don't match",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Invalid or expired reset token",
                          "path": "/api/v1/auth/reset-password"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("POST /api/v1/auth/reset-password - Password reset attempt with token");
        MessageResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Change password for authenticated user
     */
    @PostMapping("/change-password")
    @Operation(
        summary = "Change password",
        description = """
            Change your password when already authenticated.

            **Requirements:**
            1. Must be authenticated (provide valid JWT token)
            2. Must provide correct current password
            3. New password must be different from current password
            4. New passwords must match

            **Note:** This endpoint REQUIRES authentication (Bearer token).
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password successfully changed",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Change successful",
                    value = """
                        {
                          "message": "Password has been successfully changed.",
                          "success": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Current password incorrect or validation failed",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Current password is incorrect",
                          "path": "/api/v1/auth/change-password"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing JWT token",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "timestamp": "2025-01-15T10:30:00",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Full authentication is required to access this resource",
                          "path": "/api/v1/auth/change-password"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<MessageResponse> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("POST /api/v1/auth/change-password - Password change request");

        // Extract officer ID from JWT token
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        UUID officerId = UUID.fromString(jwtUtil.extractClaim(token, claims -> claims.get("officerId", String.class)));

        MessageResponse response = authService.changePassword(officerId, request);
        return ResponseEntity.ok(response);
    }
}
