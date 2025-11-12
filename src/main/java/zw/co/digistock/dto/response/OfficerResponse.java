package zw.co.digistock.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for Officer information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Officer information response")
public class OfficerResponse {

    @Schema(description = "Unique identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Unique officer code", example = "AGRI-HA-001")
    private String officerCode;

    @Schema(description = "First name", example = "Tendai")
    private String firstName;

    @Schema(description = "Last name", example = "Katsande")
    private String lastName;

    @Schema(description = "Full name (computed)", example = "Tendai Katsande")
    private String fullName;

    @Schema(description = "Email address", example = "tendai.katsande@agritex.gov.zw")
    private String email;

    @Schema(description = "Phone number", example = "+263712345678")
    private String phoneNumber;

    @Schema(description = "Officer role", example = "AGRITEX_OFFICER")
    private UserRole role;

    @Schema(description = "Province of operation", example = "Harare")
    private String province;

    @Schema(description = "District of operation", example = "02")
    private String district;

    @Schema(description = "Profile photo reference in MinIO", example = "minio://digistock-officers/550e8400/photo.jpg")
    private String photoRef;

    @Schema(description = "Whether biometric enrollment is complete")
    private boolean biometricEnrolled;

    @Schema(description = "Whether the officer account is active")
    private boolean active;

    @Schema(description = "Number of fingerprints enrolled")
    private int fingerprintCount;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Statistics about officer activities")
    private OfficerStats stats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Officer activity statistics")
    public static class OfficerStats {
        @Schema(description = "Number of clearances issued (police officers only)")
        private long clearancesIssued;

        @Schema(description = "Number of permits issued (AGRITEX officers only)")
        private long permitsIssued;
    }
}
