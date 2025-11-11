package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.PermitStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Movement Permit response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitResponse {

    private UUID id;
    private String permitNumber;
    private PermitStatus status;
    private boolean valid;

    private String fromLocation;
    private String toLocation;
    private String purpose;
    private String transportMode;
    private String vehicleNumber;
    private String driverName;

    private LocalDateTime issuedAt;
    private LocalDate validFrom;
    private LocalDate validUntil;

    private ClearanceSummary clearance;
    private LivestockSummary livestock;
    private OfficerSummary issuedBy;

    private String qrRef;
    private String pdfRef;

    private Double issueLatitude;
    private Double issueLongitude;

    private LocalDateTime completedAt;
    private Double completionLatitude;
    private Double completionLongitude;

    private int verificationCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClearanceSummary {
        private UUID id;
        private String clearanceNumber;
        private LocalDate expiryDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LivestockSummary {
        private UUID id;
        private String tagCode;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfficerSummary {
        private UUID id;
        private String officerCode;
        private String fullName;
        private String role;
    }
}
