package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find permits for a specific livestock (paginated)
     */
    Page<MovementPermit> findByLivestockId(UUID livestockId, Pageable pageable);

    /**
     * Find permits by status (paginated)
     */
    Page<MovementPermit> findByStatus(PermitStatus status, Pageable pageable);

    /**
     * Find permits issued by a specific officer (paginated)
     */
    Page<MovementPermit> findByIssuedById(UUID officerId, Pageable pageable);

    /**
     * Find permits by clearance (paginated)
     */
    Page<MovementPermit> findByClearanceId(UUID clearanceId, Pageable pageable);

    /**
     * Find valid (approved and not expired) permits (paginated)
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.status = 'APPROVED' " +
           "AND p.validFrom <= :today AND p.validUntil >= :today")
    Page<MovementPermit> findValidPermits(LocalDate today, Pageable pageable);

    /**
     * Find expired permits (paginated)
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.status = 'APPROVED' AND p.validUntil < :today")
    Page<MovementPermit> findExpiredPermits(LocalDate today, Pageable pageable);

    /**
     * Find permits in transit (paginated)
     */
    Page<MovementPermit> findByStatusOrderByIssuedAtDesc(PermitStatus status, Pageable pageable);

    /**
     * Find permits by destination district (paginated)
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.toLocation LIKE %:district%")
    Page<MovementPermit> findByDestinationDistrict(String district, Pageable pageable);

    /**
     * Find recent permits (last 30 days) - paginated
     */
    @Query("SELECT p FROM MovementPermit p WHERE p.issuedAt >= :since ORDER BY p.issuedAt DESC")
    Page<MovementPermit> findRecentPermits(java.time.LocalDateTime since, Pageable pageable);

    /**
     * Check if permit number exists
     */
    boolean existsByPermitNumber(String permitNumber);

    /**
     * Count permits by status
     */
    long countByStatus(PermitStatus status);

    /**
     * Count permits issued within a time range
     */
    @Query("SELECT COUNT(p) FROM MovementPermit p WHERE p.issuedAt >= :start AND p.issuedAt < :end")
    long countIssuedBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
