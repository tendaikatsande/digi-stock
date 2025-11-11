package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.Owner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Livestock entity operations
 */
@Repository
public interface LivestockRepository extends JpaRepository<Livestock, UUID> {

    /**
     * Find livestock by unique tag code
     */
    Optional<Livestock> findByTagCode(String tagCode);

    /**
     * Find all livestock owned by a specific owner
     */
    List<Livestock> findByOwner(Owner owner);

    /**
     * Find livestock by owner ID
     */
    List<Livestock> findByOwnerId(UUID ownerId);

    /**
     * Find livestock by breed
     */
    List<Livestock> findByBreed(String breed);

    /**
     * Find livestock by mother (offspring of a specific animal)
     */
    List<Livestock> findByMotherId(UUID motherId);

    /**
     * Find livestock by father (offspring of a specific animal)
     */
    List<Livestock> findByFatherId(UUID fatherId);

    /**
     * Find stolen livestock
     */
    List<Livestock> findByStolen(boolean stolen);

    /**
     * Find stolen livestock in a specific district
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE l.stolen = true AND o.district = :district")
    List<Livestock> findStolenByDistrict(String district);

    /**
     * Search livestock by tag code pattern (e.g., "HA-02-*")
     */
    @Query("SELECT l FROM Livestock l WHERE l.tagCode LIKE :pattern")
    List<Livestock> findByTagCodePattern(String pattern);

    /**
     * Find livestock registered in a specific province
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE o.province = :province")
    List<Livestock> findByProvince(String province);

    /**
     * Find livestock registered in a specific district
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE o.district = :district")
    List<Livestock> findByDistrict(String district);

    /**
     * Check if tag code already exists
     */
    boolean existsByTagCode(String tagCode);

    /**
     * Count livestock by owner
     */
    long countByOwnerId(UUID ownerId);

    /**
     * Count stolen livestock
     */
    long countByStolen(boolean stolen);
}
