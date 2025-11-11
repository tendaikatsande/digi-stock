package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.ClearanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a police clearance for livestock movement.
 * A clearance verifies ownership legitimacy and confirms the animal is not stolen.
 * Required before a movement permit can be issued.
 */
@Entity
@Table(name = "police_clearances", indexes = {
    @Index(name = "idx_clearance_number", columnList = "clearance_number", unique = true),
    @Index(name = "idx_clearance_livestock", columnList = "livestock_id"),
    @Index(name = "idx_clearance_owner", columnList = "owner_id"),
    @Index(name = "idx_clearance_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliceClearance extends BaseEntity {

    /**
     * Unique clearance reference number
     * Format: PC-{PROVINCE_CODE}-{SEQUENTIAL}
     * Example: PC-HA-000123
     */
    @Column(name = "clearance_number", unique = true, nullable = false, length = 50)
    private String clearanceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id", nullable = false)
    private Livestock livestock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    /**
     * Police officer who processed this clearance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private Officer issuedBy;

    /**
     * Fingerprint reference of the officer who signed this clearance
     * (stored in MinIO as proof of authenticity)
     */
    @Column(name = "officer_fingerprint_ref", length = 500)
    private String officerFingerprintRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ClearanceStatus status;

    /**
     * Reason for rejection (if status = REJECTED)
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /**
     * Date and time when clearance was granted/rejected
     */
    @Column(name = "clearance_date")
    private LocalDateTime clearanceDate;

    /**
     * Expiry date of this clearance (typically 7-14 days from issuance)
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * MinIO reference to generated clearance PDF
     */
    @Column(name = "pdf_ref", length = 500)
    private String pdfRef;

    /**
     * MinIO reference to clearance QR code
     */
    @Column(name = "qr_ref", length = 500)
    private String qrRef;

    /**
     * GPS coordinates where clearance was issued (for audit trail)
     */
    @Column(name = "issue_latitude")
    private Double issueLatitude;

    @Column(name = "issue_longitude")
    private Double issueLongitude;

    /**
     * Movement permits linked to this clearance
     */
    @OneToMany(mappedBy = "clearance", cascade = CascadeType.ALL)
    private java.util.List<MovementPermit> permits = new java.util.ArrayList<>();

    /**
     * Check if clearance is currently valid
     */
    public boolean isValid() {
        return status == ClearanceStatus.APPROVED
            && expiryDate != null
            && !expiryDate.isBefore(LocalDate.now());
    }
}
