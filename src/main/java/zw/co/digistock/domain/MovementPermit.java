package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.PermitStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a livestock movement permit.
 * Authorizes movement of cattle between specified locations within defined dates.
 * Requires a valid police clearance.
 */
@Entity
@Table(name = "movement_permits", indexes = {
    @Index(name = "idx_permit_number", columnList = "permit_number", unique = true),
    @Index(name = "idx_permit_livestock", columnList = "livestock_id"),
    @Index(name = "idx_permit_clearance", columnList = "clearance_id"),
    @Index(name = "idx_permit_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovementPermit extends BaseEntity {

    /**
     * Unique permit number
     * Format: DG-{YEAR}-{SEQUENTIAL}
     * Example: DG-2025-000123
     */
    @Column(name = "permit_number", unique = true, nullable = false, length = 50)
    private String permitNumber;

    /**
     * Police clearance that this permit is based on
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clearance_id", nullable = false)
    private PoliceClearance clearance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id", nullable = false)
    private Livestock livestock;

    /**
     * Origin location (district, ward, village)
     */
    @Column(name = "from_location", nullable = false, columnDefinition = "TEXT")
    private String fromLocation;

    /**
     * Destination location (district, ward, village)
     */
    @Column(name = "to_location", nullable = false, columnDefinition = "TEXT")
    private String toLocation;

    /**
     * Purpose of movement (e.g., "Sale", "Auction", "Grazing", "Slaughter")
     */
    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    /**
     * Mode of transport (e.g., "Truck", "On foot", "Trailer")
     */
    @Column(name = "transport_mode", length = 100)
    private String transportMode;

    /**
     * Vehicle registration number (if applicable)
     */
    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber;

    /**
     * Driver name (if applicable)
     */
    @Column(name = "driver_name", length = 200)
    private String driverName;

    /**
     * AGRITEX officer who issued this permit
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private Officer issuedBy;

    /**
     * Date and time when permit was issued
     */
    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    /**
     * Start date of validity
     */
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    /**
     * End date of validity
     */
    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PermitStatus status;

    /**
     * MinIO reference to QR code for this permit
     */
    @Column(name = "qr_ref", length = 500)
    private String qrRef;

    /**
     * MinIO reference to signed PDF permit
     */
    @Column(name = "pdf_ref", length = 500)
    private String pdfRef;

    /**
     * Fingerprint reference of officer who signed this permit
     */
    @Column(name = "signed_fingerprint_ref", length = 500)
    private String signedFingerprintRef;

    /**
     * GPS coordinates where permit was issued
     */
    @Column(name = "issue_latitude")
    private Double issueLatitude;

    @Column(name = "issue_longitude")
    private Double issueLongitude;

    /**
     * GPS coordinates where movement was verified/completed
     */
    @Column(name = "completion_latitude")
    private Double completionLatitude;

    @Column(name = "completion_longitude")
    private Double completionLongitude;

    /**
     * Date/time when movement was completed
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Verification checkpoints (for tracking permit scans at roadblocks)
     */
    @OneToMany(mappedBy = "permit", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PermitVerification> verifications = new java.util.ArrayList<>();

    /**
     * Check if permit is currently valid
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return status == PermitStatus.APPROVED
            && !validFrom.isAfter(today)
            && !validUntil.isBefore(today);
    }
}
