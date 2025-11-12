package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a livestock owner in the DigiStock system.
 * Owners can register their cattle, request movement permits,
 * and are identified via biometric fingerprint enrollment.
 */
@Entity
@Table(name = "owners", indexes = {
    @Index(name = "idx_owner_national_id", columnList = "national_id"),
    @Index(name = "idx_owner_phone", columnList = "phone_number"),
    @Index(name = "idx_owner_district", columnList = "district")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Owner extends BaseEntity {

    @Column(name = "national_id", unique = true, nullable = false, length = 50)
    private String nationalId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "ward", length = 100)
    private String ward;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "province", nullable = false, length = 100)
    private String province;

    /**
     * References to fingerprint templates stored in MinIO.
     * Format: ["minio://digistock-fingerprints/owners/{ownerId}/left-thumb.fpt",
     *          "minio://digistock-fingerprints/owners/{ownerId}/right-thumb.fpt"]
     */
    @ElementCollection
    @CollectionTable(name = "owner_fingerprint_refs", joinColumns = @JoinColumn(name = "owner_id"))
    @Column(name = "fingerprint_ref", length = 500)
    private List<String> fingerprintRefs = new ArrayList<>();

    /**
     * Reference to owner's photo in MinIO (for visual verification)
     */
    @Column(name = "photo_ref", length = 500)
    private String photoRef;

    /**
     * Whether the owner's biometrics have been enrolled
     */
    @Column(name = "biometric_enrolled", nullable = false)
    private boolean biometricEnrolled = false;

    /**
     * All livestock owned by this owner
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Livestock> livestock = new ArrayList<>();

    /**
     * Convenience methods
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addFingerprint(String fingerprintRef) {
        if (this.fingerprintRefs == null) {
            this.fingerprintRefs = new ArrayList<>();
        }
        this.fingerprintRefs.add(fingerprintRef);
    }
}
