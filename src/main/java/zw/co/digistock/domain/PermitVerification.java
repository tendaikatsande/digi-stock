package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * Represents a verification checkpoint scan of a movement permit.
 * Created whenever police scan a permit QR code at a roadblock.
 * Provides an audit trail of livestock movement.
 */
@Entity
@Table(name = "permit_verifications", indexes = {
    @Index(name = "idx_verification_permit", columnList = "permit_id"),
    @Index(name = "idx_verification_officer", columnList = "verified_by"),
    @Index(name = "idx_verification_time", columnList = "verified_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVerification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permit_id", nullable = false)
    private MovementPermit permit;

    /**
     * Police officer who performed the verification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by", nullable = false)
    private Officer verifiedBy;

    /**
     * Date and time of verification
     */
    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    /**
     * GPS location where verification occurred (roadblock location)
     */
    @Column(name = "verification_latitude")
    private Double verificationLatitude;

    @Column(name = "verification_longitude")
    private Double verificationLongitude;

    /**
     * Location description (e.g., "Marondera Roadblock")
     */
    @Column(name = "location_description", columnDefinition = "TEXT")
    private String locationDescription;

    /**
     * Notes added by officer during verification
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Whether the verification result was valid (true) or raised a flag (false)
     */
    @Column(name = "is_valid", nullable = false)
    private boolean valid;

    /**
     * Reason if flagged as invalid
     */
    @Column(name = "flag_reason", columnDefinition = "TEXT")
    private String flagReason;
}
