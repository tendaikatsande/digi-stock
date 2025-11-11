package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.ClearanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Police Clearance response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClearanceResponse {

    private UUID id;
    private String clearanceNumber;
    private ClearanceStatus status;
    private LocalDateTime clearanceDate;
    private LocalDate expiryDate;
    private boolean valid;

    private LivestockSummary livestock;
    private OwnerSummary owner;
    private OfficerSummary issuedBy;

    private String rejectionReason;
    private String qrRef;
    private String pdfRef;

    private Double issueLatitude;
    private Double issueLongitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
    public static class OwnerSummary {
        private UUID id;
        private String nationalId;
        private String fullName;
        private String phoneNumber;
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
