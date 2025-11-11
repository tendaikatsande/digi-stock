package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter criteria for owner search with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Owner search filter criteria")
public class OwnerSearchFilter {

    @Schema(description = "Search term for name (partial match, searches both first and last name)", example = "John")
    private String name;

    @Schema(description = "National ID filter (exact match)", example = "63-123456A12")
    private String nationalId;

    @Schema(description = "Phone number filter (partial match)", example = "+263771234567")
    private String phoneNumber;

    @Schema(description = "Email filter (partial match)", example = "john@example.com")
    private String email;

    @Schema(description = "District filter (exact match)", example = "Harare")
    private String district;

    @Schema(description = "Province filter (exact match)", example = "Harare")
    private String province;

    @Schema(description = "Has fingerprint enrolled (true/false/null for all)")
    private Boolean hasFingerprint;

    @Schema(description = "Has photo uploaded (true/false/null for all)")
    private Boolean hasPhoto;
}
