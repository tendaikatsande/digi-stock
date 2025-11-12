package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

/**
 * Represents a ward within a district.
 * Third-level administrative division.
 */
@Entity
@Table(name = "wards", indexes = {
    @Index(name = "idx_ward_name", columnList = "name"),
    @Index(name = "idx_ward_code", columnList = "code", unique = true),
    @Index(name = "idx_ward_district", columnList = "district_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Ward extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
