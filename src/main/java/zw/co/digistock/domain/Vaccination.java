package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;
import zw.co.digistock.domain.enums.UserRole;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a vaccination record for a livestock animal.
 * Tracks vaccine type, administration date, veterinary officer, and batch information.
 */
@Entity
@Table(name = "vaccinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class
Vaccination extends BaseEntity {


    /**
     * Type of vaccine administered (e.g., Foot and Mouth Disease, Anthrax)
     */
    @Column(nullable = false, length = 255)
    private String vaccineType;

    /**
     * Date when the vaccine was administered
     */
    @Column(nullable = false)
    private LocalDate vaccinationDate;

    /**
     * Batch number of the vaccine
     */
    @Column(length = 100)
    private String batchNumber;

    /**
     * Next scheduled vaccination date (for booster shots)
     */
    private LocalDate nextVaccinationDate;

    /**
     * Notes or observations about the vaccination (e.g., side effects)
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Veterinary officer who administered the vaccine
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinary_officer_id", nullable = false)
    private Officer veterinaryOfficer;

    /**
     * Livestock animal that received the vaccination
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id", nullable = false)
    private Livestock livestock;

    /**
     * Location where the vaccination was administered (province, district, ward)
     */
    @Column(length = 255)
    private String location;

    /**
     * GPS coordinates of the vaccination location (latitude, longitude)
     */
    private String gpsCoordinates;

    /**
     * Method of administration (e.g., injection, oral)
     */
    @Column(length = 100)
    private String administrationMethod;

    /**
     * Manufacturer of the vaccine
     */
    @Column(length = 255)
    private String manufacturer;

    /**
     * Lot number of the vaccine (additional tracking information)
     */
    @Column(length = 100)
    private String lotNumber;

    /**
     * Expiry date of the vaccine batch
     */
    private LocalDate vaccineExpiryDate;

    /**
     * Dose administered (e.g., 1ml, 2 tablets)
     */
    @Column(length = 50)
    private String dose;

    /**
     * Temperature at which the vaccine was stored
     */
    @Column(length = 50)
    private String storageTemperature;

    /**
     * Confirmation that the vaccination was properly recorded
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerified = false;

    /**
     * Officer who verified the vaccination record
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private Officer verifiedBy;

    /**
     * Date when the vaccination record was verified
     */
    private LocalDate verifiedDate;
}
