package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.MovementPermit;
import zw.co.digistock.domain.enums.PermitStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for MovementPermit entity operations
 */
@Repository
public interface MovementPermitRepository extends JpaRepository<MovementPermit, UUID> {

    /**
     * Find permit by permit number
     */
    Optional<MovementPermit> findByPermitNumber(String permitNumber);

    /**
     * Find permits for a specific livestock
     */
    List<MovementPermit> findByLivestockId(UUID livestockId);

    /**
     * Find permits by status
     */
    List<MovementPermit> findByStatus(PermitStatus status);

    /**
     * Find permits issued by a specific officer
     */
    List<MovementPermit> findByIssuedById(UUID officerId);

    /**
     * Find permits by clearance
     */
    List<MovementPermit> findByClearanceId(UUID clearanceId);

    /**
     * Find valid (approved and not expired) permits
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.status = 'APPROVED' " +
           "AND p.validFrom <= :today AND p.validUntil >= :today")
    List<MovementPermit> findValidPermits(LocalDate today);

    /**
     * Find expired permits
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.status = 'APPROVED' AND p.validUntil < :today")
    List<MovementPermit> findExpiredPermits(LocalDate today);

    /**
     * Find permits in transit
     */
    List<MovementPermit> findByStatusOrderByIssuedAtDesc(PermitStatus status);

    /**
     * Find permits by destination district
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.toLocation LIKE %:district%")
    List<MovementPermit> findByDestinationDistrict(String district);

    /**
     * Find recent permits (last 30 days)
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.issuedAt >= :since ORDER BY p.issuedAt DESC")
    List<MovementPermit> findRecentPermits(java.time.LocalDateTime since);

    /**
     * Check if permit number exists
     */
    boolean existsByPermitNumber(String permitNumber);

    /**
     * Count permits by status
     */
    long countByStatus(PermitStatus status);
}
