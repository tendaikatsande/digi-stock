package zw.co.digistock.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.UserRole;

import java.util.UUID;

/**
 * Response DTO for authentication operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response with JWT token and officer details")
public class AuthResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiration time in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Officer ID")
    private UUID officerId;

    @Schema(description = "Officer email", example = "admin@digistock.zw")
    private String email;

    @Schema(description = "Officer full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Officer code", example = "AG-001")
    private String officerCode;

    @Schema(description = "Officer role", example = "ADMIN")
    private UserRole role;

    @Schema(description = "Province", example = "Harare")
    private String province;

    @Schema(description = "District", example = "Harare")
    private String district;

    @Schema(description = "Whether officer account is active", example = "true")
    private boolean active;
}
