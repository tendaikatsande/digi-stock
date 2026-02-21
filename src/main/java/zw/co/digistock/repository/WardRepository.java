package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.District;
import zw.co.digistock.domain.Ward;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Ward entity operations
 */
@Repository
public interface WardRepository extends JpaRepository<Ward, UUID> {

    /**
     * Find ward by name
     */
    Optional<Ward> findByName(String name);

    /**
     * Find ward by code
     */
    Optional<Ward> findByCode(String code);

    /**
     * Find ward by district and code
     */
    Optional<Ward> findByDistrictAndCode(District district, String code);

    /**
     * Find all wards in a district
     */
    @Query("SELECT w FROM Ward w WHERE w.district.id = :districtId AND w.active = true")
    List<Ward> findByDistrictId(UUID districtId);

    /**
     * Find all active wards
     */
    List<Ward> findByActiveTrue();

    /**
     * Check if ward code already exists
     */
    boolean existsByCode(String code);
}
