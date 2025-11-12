package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Officer authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate officer and get JWT token
     */
    @PostMapping("/login")
    @Operation(
        summary = "Officer login",
        description = "Authenticate officer with email and password, returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error"
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
        description = "Create a new officer account with email, password, and role"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully registered",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email or officer code already exists"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error"
        )
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterOfficerRequest request) {
        log.info("POST /api/v1/auth/register - Registration request for: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
