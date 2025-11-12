package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating officer information
 * Note: Role and officer code cannot be changed after creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update officer information")
public class UpdateOfficerRequest {

    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Schema(description = "First name", example = "Tendai")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Schema(description = "Last name", example = "Katsande")
    private String lastName;

    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "tendai.katsande@agritex.gov.zw")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    @Schema(description = "Phone number", example = "+263712345678")
    private String phoneNumber;

    @Schema(description = "Province of operation", example = "Harare")
    private String province;

    @Schema(description = "District of operation", example = "02")
    private String district;

    @Schema(description = "Whether the officer account is active")
    private Boolean active;
}
