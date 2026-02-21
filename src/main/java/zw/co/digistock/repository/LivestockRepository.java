package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find livestock by owner ID (paginated)
     */
    Page<Livestock> findByOwnerId(UUID ownerId, Pageable pageable);

    /**
     * Find livestock by breed (paginated)
     */
    Page<Livestock> findByBreed(String breed, Pageable pageable);

    /**
     * Find livestock by mother (offspring of a specific animal) - paginated
     */
    Page<Livestock> findByMotherId(UUID motherId, Pageable pageable);

    /**
     * Find livestock by father (offspring of a specific animal) - paginated
     */
    Page<Livestock> findByFatherId(UUID fatherId, Pageable pageable);

    /**
     * Find stolen livestock (paginated)
     */
    Page<Livestock> findByStolen(boolean stolen, Pageable pageable);

    /**
     * Find stolen livestock in a specific district (paginated)
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE l.stolen = true AND o.district = :district")
    Page<Livestock> findStolenByDistrict(String district, Pageable pageable);

    /**
     * Search livestock by tag code pattern (e.g., "HA-02-*") - paginated
     */
    @Query("SELECT l FROM Livestock l WHERE l.tagCode LIKE :pattern")
    Page<Livestock> findByTagCodePattern(String pattern, Pageable pageable);

    /**
     * Find livestock registered in a specific province (paginated)
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE o.province = :province")
    Page<Livestock> findByProvince(String province, Pageable pageable);

    /**
     * Find livestock registered in a specific district (paginated)
     */
    @Query("SELECT l FROM Livestock l JOIN l.owner o WHERE o.district = :district")
    Page<Livestock> findByDistrict(String district, Pageable pageable);

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

    /**
     * Count livestock grouped by owner province
     */
    @Query("SELECT o.province, COUNT(l) FROM Livestock l JOIN l.owner o GROUP BY o.province")
    List<Object[]> countGroupByProvince();

    /**
     * Count livestock grouped by breed
     */
    @Query("SELECT l.breed, COUNT(l) FROM Livestock l GROUP BY l.breed")
    List<Object[]> countGroupByBreed();
}
