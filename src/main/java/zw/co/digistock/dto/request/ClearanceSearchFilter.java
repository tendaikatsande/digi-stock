package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.ClearanceStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Filter criteria for police clearance search with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Police clearance search filter criteria")
public class ClearanceSearchFilter {

    @Schema(description = "Clearance number filter (exact match)", example = "PC-HR-000001")
    private String clearanceNumber;

    @Schema(description = "Clearance status filter", example = "APPROVED")
    private ClearanceStatus status;

    @Schema(description = "Livestock UUID filter")
    private UUID livestockId;

    @Schema(description = "Owner UUID filter")
    private UUID ownerId;

    @Schema(description = "Issuing officer UUID filter")
    private UUID issuedById;

    @Schema(description = "Valid on date (checks if clearance is valid on this date)", example = "2025-11-15")
    private LocalDate validOn;

    @Schema(description = "Clearance date from (inclusive)", example = "2025-01-01")
    private LocalDate clearanceDateFrom;

    @Schema(description = "Clearance date to (inclusive)", example = "2025-12-31")
    private LocalDate clearanceDateTo;

    @Schema(description = "Expiry date from (inclusive)", example = "2025-11-01")
    private LocalDate expiryDateFrom;

    @Schema(description = "Expiry date to (inclusive)", example = "2025-11-30")
    private LocalDate expiryDateTo;

    @Schema(description = "Is valid now (non-expired clearances)", example = "true")
    private Boolean isValid;

    @Schema(description = "Province where issued", example = "Harare")
    private String province;
}
