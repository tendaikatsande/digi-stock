package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import zw.co.digistock.domain.base.BaseEntity;

/**
 * Represents a photo of a livestock animal.
 * Multiple photos can be attached to each animal (front, side, brand close-up, etc.)
 */
@Entity
@Table(name = "livestock_photos", indexes = {
    @Index(name = "idx_livestock_photo_livestock", columnList = "livestock_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivestockPhoto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_id", nullable = false)
    private Livestock livestock;

    /**
     * MinIO reference to the photo
     * Format: minio://digistock-livestock-photos/{livestockId}/{photoId}.jpg
     */
    @Column(name = "photo_ref", nullable = false, length = 500)
    private String photoRef;

    /**
     * Description of photo (e.g., "Front view", "Left side", "Brand close-up")
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Type of photo (REGISTRATION, BRAND, INSPECTION, etc.)
     */
    @Column(name = "photo_type", length = 50)
    private String photoType;
}
