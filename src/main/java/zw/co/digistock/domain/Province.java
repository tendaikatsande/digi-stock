package zw.co.digistock.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import zw.co.digistock.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a province in Zimbabwe.
 * Top-level administrative division.
 */
@Entity
@Table(name = "provinces", indexes = {
    @Index(name = "idx_province_name", columnList = "name", unique = true),
    @Index(name = "idx_province_code", columnList = "code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Province extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    /**
     * Districts in this province
     */
    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<District> districts = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
