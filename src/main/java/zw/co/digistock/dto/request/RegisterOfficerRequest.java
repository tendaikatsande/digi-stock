package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.UserRole;

/**
 * Request DTO for officer registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Officer registration request")
public class RegisterOfficerRequest {

    @NotBlank(message = "Officer code is required")
    @Schema(description = "Unique officer code", example = "AG-001")
    private String officerCode;

    @NotBlank(message = "First name is required")
    @Schema(description = "Officer first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Officer last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Officer email address", example = "john.doe@agritex.gov.zw")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+263[0-9]{9}$", message = "Phone number must be in format +263XXXXXXXXX")
    @Schema(description = "Officer phone number", example = "+263771234567")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
    )
    @Schema(description = "Officer password (min 8 chars, must include uppercase, lowercase, number, special char)", example = "SecurePass123!")
    private String password;

    @NotNull(message = "Role is required")
    @Schema(description = "Officer role", example = "AGRITEX_OFFICER")
    private UserRole role;

    @Schema(description = "Province where officer is stationed", example = "Harare")
    private String province;

    @Schema(description = "District where officer is stationed", example = "Harare")
    private String district;
}
