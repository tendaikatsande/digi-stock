package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.TransferStatus;

import java.time.LocalDate;

@Entity
@Table(name = "ownership_transfers", indexes = {
    @Index(name = "idx_transfer_livestock", columnList = "livestock_id"),
    @Index(name = "idx_transfer_from_owner", columnList = "from_owner_id"),
    @Index(name = "idx_transfer_to_owner", columnList = "to_owner_id"),
    @Index(name = "idx_transfer_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OwnershipTransfer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id", nullable = false)
    private Livestock livestock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_owner_id", nullable = false)
    private Owner fromOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_owner_id", nullable = false)
    private Owner toOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by_id", nullable = false)
    private Officer initiatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "from_owner_confirmed", nullable = false)
    @Builder.Default
    private boolean fromOwnerConfirmed = false;

    @Column(name = "to_owner_confirmed", nullable = false)
    @Builder.Default
    private boolean toOwnerConfirmed = false;

    @Column(name = "from_owner_fingerprint_ref", length = 500)
    private String fromOwnerFingerprintRef;

    @Column(name = "to_owner_fingerprint_ref", length = 500)
    private String toOwnerFingerprintRef;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
}
