package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for forgot password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Forgot password request with email")
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Officer email address", example = "john.doe@agritex.gov.zw")
    private String email;
}
