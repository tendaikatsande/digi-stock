package zw.co.digistock.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.UserRole;

import java.util.UUID;

/**
 * Response DTO for authentication operations.
 * Supports both Officer and Owner login via unified AppUser.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response with JWT token and user details")
public class AuthResponse {

    @Schema(description = "JWT access token")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiration time in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "User ID (AppUser.id — works for both Officers and Owners)")
    private UUID userId;

    @Schema(description = "User type: OFFICER or OWNER", example = "OFFICER")
    private String userType;

    @Schema(description = "Email address")
    private String email;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Officer code (only present for OFFICER users)", example = "AG-001")
    private String officerCode;

    @Schema(description = "User role", example = "AGRITEX_OFFICER")
    private UserRole role;

    @Schema(description = "Province", example = "Harare")
    private String province;

    @Schema(description = "District", example = "Harare Central")
    private String district;

    @Schema(description = "Whether account is active", example = "true")
    private boolean active;
}
