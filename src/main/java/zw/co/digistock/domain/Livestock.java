package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single head of cattle in the DigiStock system.
 * Each animal has a unique tag code, biometric photos, and parentage tracking.
 */
@Entity
@Table(name = "livestock", indexes = {
    @Index(name = "idx_livestock_tag_code", columnList = "tag_code", unique = true),
    @Index(name = "idx_livestock_owner", columnList = "owner_id"),
    @Index(name = "idx_livestock_mother", columnList = "mother_id"),
    @Index(name = "idx_livestock_father", columnList = "father_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Livestock extends BaseEntity {

    /**
     * Unique hierarchical tag code
     * Format: {PROVINCE}-{DISTRICT}-{WARD}-{SERIAL}
     * Example: HA-02-012-0234
     */
    @Column(name = "tag_code", unique = true, nullable = false, length = 32)
    private String tagCode;

    /**
     * Optional name given to the animal by owner
     */
    @Column(name = "name", length = 100)
    private String name;

    /**
     * Breed (e.g., Brahman, Tuli, Mashona, etc.)
     */
    @Column(name = "breed", length = 100)
    private String breed;

    /**
     * Sex: M (Male), F (Female), C (Castrated/Steer)
     */
    @Column(name = "sex", length = 10)
    private String sex;

    /**
     * Date of birth
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Primary color (e.g., Brown, White, Black, Mixed)
     */
    @Column(name = "color", length = 100)
    private String color;

    /**
     * Distinguishing marks (e.g., "white patch on left hind leg")
     */
    @Column(name = "distinguishing_marks", columnDefinition = "TEXT")
    private String distinguishingMarks;

    /**
     * Owner of this animal
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    /**
     * Mother (dam) - for parentage tracking
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_id")
    private Livestock mother;

    /**
     * Father (sire) - for parentage tracking
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "father_id")
    private Livestock father;

    /**
     * Children (offspring) of this animal
     */
    @OneToMany(mappedBy = "mother", cascade = CascadeType.ALL)
    private List<Livestock> offspringAsMother = new ArrayList<>();

    @OneToMany(mappedBy = "father", cascade = CascadeType.ALL)
    private List<Livestock> offspringAsFather = new ArrayList<>();

    /**
     * GPS coordinates where animal was registered
     */
    @Column(name = "registration_latitude")
    private Double registrationLatitude;

    @Column(name = "registration_longitude")
    private Double registrationLongitude;

    /**
     * Photos of this livestock stored in MinIO
     */
    @OneToMany(mappedBy = "livestock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivestockPhoto> photos = new ArrayList<>();

    /**
     * Police clearances for this animal
     */
    @OneToMany(mappedBy = "livestock", cascade = CascadeType.ALL)
    private List<PoliceClearance> clearances = new ArrayList<>();

    /**
     * Movement permits for this animal
     */
    @OneToMany(mappedBy = "livestock", cascade = CascadeType.ALL)
    private List<MovementPermit> permits = new ArrayList<>();

    /**
     * Whether this animal has been reported stolen
     */
    @Column(name = "is_stolen", nullable = false)
    private boolean stolen = false;

    /**
     * Date reported stolen (if applicable)
     */
    @Column(name = "stolen_date")
    private LocalDate stolenDate;

    public void addPhoto(LivestockPhoto photo) {
        photos.add(photo);
        photo.setLivestock(this);
    }

    public void removePhoto(LivestockPhoto photo) {
        photos.remove(photo);
        photo.setLivestock(null);
    }

    /**
     * Get all offspring (from both mother and father relationships)
     */
    public List<Livestock> getAllOffspring() {
        List<Livestock> all = new ArrayList<>();
        if (offspringAsMother != null) all.addAll(offspringAsMother);
        if (offspringAsFather != null) all.addAll(offspringAsFather);
        return all;
    }
}
