package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a district within a province.
 * Second-level administrative division.
 */
@Entity
@Table(name = "districts", indexes = {
    @Index(name = "idx_district_name", columnList = "name"),
    @Index(name = "idx_district_code", columnList = "code", unique = true),
    @Index(name = "idx_district_province", columnList = "province_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class District extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    /**
     * Wards in this district
     */
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ward> wards = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
