package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.District;
import zw.co.digistock.domain.Province;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for District entity operations
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {

    /**
     * Find district by name
     */
    Optional<District> findByName(String name);

    /**
     * Find district by code
     */
    Optional<District> findByCode(String code);

    /**
     * Find district by province and code
     */
    Optional<District> findByProvinceAndCode(Province province, String code);

    /**
     * Find all districts in a province
     */
    @Query("SELECT d FROM District d WHERE d.province.id = :provinceId AND d.active = true")
    List<District> findByProvinceId(UUID provinceId);

    /**
     * Find all active districts
     */
    List<District> findByActiveTrue();

    /**
     * Check if district code already exists
     */
    boolean existsByCode(String code);
}
