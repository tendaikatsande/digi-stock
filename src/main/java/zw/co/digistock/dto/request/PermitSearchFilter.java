package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.PermitStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Filter criteria for movement permit search with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movement permit search filter criteria")
public class PermitSearchFilter {

    @Schema(description = "Permit number filter (exact match)", example = "MP-HR-000001")
    private String permitNumber;

    @Schema(description = "Permit status filter", example = "ACTIVE")
    private PermitStatus status;

    @Schema(description = "Livestock UUID filter")
    private UUID livestockId;

    @Schema(description = "Owner UUID filter")
    private UUID ownerId;

    @Schema(description = "Issuing officer UUID filter")
    private UUID issuedById;

    @Schema(description = "Origin district filter", example = "Harare")
    private String originDistrict;

    @Schema(description = "Destination district filter", example = "Bulawayo")
    private String destinationDistrict;

    @Schema(description = "Purpose filter (partial match)", example = "Sale")
    private String purpose;

    @Schema(description = "Valid on date (checks if permit is valid on this date)", example = "2025-11-15")
    private LocalDate validOn;

    @Schema(description = "Issued date from (inclusive)", example = "2025-01-01")
    private LocalDate issuedFrom;

    @Schema(description = "Issued date to (inclusive)", example = "2025-12-31")
    private LocalDate issuedTo;

    @Schema(description = "Valid until from (inclusive)", example = "2025-11-01")
    private LocalDate validUntilFrom;

    @Schema(description = "Valid until to (inclusive)", example = "2025-11-30")
    private LocalDate validUntilTo;

    @Schema(description = "Is currently valid (non-expired permits)", example = "true")
    private Boolean isValid;

    @Schema(description = "Has been verified (checked at checkpoint)", example = "true")
    private Boolean hasBeenVerified;
}
