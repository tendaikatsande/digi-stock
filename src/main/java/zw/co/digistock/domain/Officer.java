package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an officer in the DigiStock system
 * (AGRITEX extension officer, Police officer, or Admin).
 */
@Entity
@Table(name = "officers", indexes = {
    @Index(name = "idx_officer_code", columnList = "officer_code"),
    @Index(name = "idx_officer_email", columnList = "email"),
    @Index(name = "idx_officer_district", columnList = "district")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Officer extends BaseEntity {

    @Column(name = "officer_code", unique = true, nullable = false, length = 50)
    private String officerCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Password hash (bcrypt)
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    /**
     * References to officer's fingerprint templates in MinIO
     */
    @ElementCollection
    @CollectionTable(name = "officer_fingerprint_refs", joinColumns = @JoinColumn(name = "officer_id"))
    @Column(name = "fingerprint_ref", length = 500)
    private List<String> fingerprintRefs = new ArrayList<>();

    /**
     * Officer photo reference in MinIO
     */
    @Column(name = "photo_ref", length = 500)
    private String photoRef;

    /**
     * Whether biometric enrollment is complete
     */
    @Column(name = "biometric_enrolled", nullable = false)
    private boolean biometricEnrolled = false;

    /**
     * Whether this officer account is active
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /**
     * Clearances issued by this officer (if POLICE_OFFICER)
     */
    @OneToMany(mappedBy = "issuedBy", cascade = CascadeType.ALL)
    private List<PoliceClearance> issuedClearances = new ArrayList<>();

    /**
     * Permits issued by this officer (if AGRITEX_OFFICER)
     */
    @OneToMany(mappedBy = "issuedBy", cascade = CascadeType.ALL)
    private List<MovementPermit> issuedPermits = new ArrayList<>();

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
