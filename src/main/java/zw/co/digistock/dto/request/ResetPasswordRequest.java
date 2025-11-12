package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for password reset
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reset password request with token and new password")
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    @Schema(description = "Password reset token received via email", example = "abc123xyz789")
    private String token;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
    )
    @Schema(description = "New password (min 8 chars, must include uppercase, lowercase, number, special char)", example = "NewSecure123!")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Confirm new password", example = "NewSecure123!")
    private String confirmPassword;
}
