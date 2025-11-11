package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.PoliceClearance;
import zw.co.digistock.domain.enums.ClearanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PoliceClearance entity operations
 */
@Repository
public interface PoliceClearanceRepository extends JpaRepository<PoliceClearance, UUID> {

    /**
     * Find clearance by clearance number
     */
    Optional<PoliceClearance> findByClearanceNumber(String clearanceNumber);

    /**
     * Find clearances for a specific livestock (paginated)
     */
    Page<PoliceClearance> findByLivestockId(UUID livestockId, Pageable pageable);

    /**
     * Find clearances by owner (paginated)
     */
    Page<PoliceClearance> findByOwnerId(UUID ownerId, Pageable pageable);

    /**
     * Find clearances by status (paginated)
     */
    Page<PoliceClearance> findByStatus(ClearanceStatus status, Pageable pageable);

    /**
     * Find clearances issued by a specific officer (paginated)
     */
    Page<PoliceClearance> findByIssuedById(UUID officerId, Pageable pageable);

    /**
     * Find valid (approved and not expired) clearances for livestock (paginated)
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.livestock.id = :livestockId " +
           "AND c.status = 'APPROVED' AND c.expiryDate >= :today ORDER BY c.clearanceDate DESC")
    Page<PoliceClearance> findValidClearancesForLivestock(UUID livestockId, LocalDate today, Pageable pageable);

    /**
     * Find most recent clearance for livestock
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.livestock.id = :livestockId " +
           "ORDER BY c.clearanceDate DESC LIMIT 1")
    Optional<PoliceClearance> findLatestClearanceForLivestock(UUID livestockId);

    /**
     * Find expired clearances (paginated)
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.status = 'APPROVED' AND c.expiryDate < :today")
    Page<PoliceClearance> findExpiredClearances(LocalDate today, Pageable pageable);

    /**
     * Find pending clearances (paginated)
     */
    Page<PoliceClearance> findByStatusOrderByClearanceDateDesc(ClearanceStatus status, Pageable pageable);

    /**
     * Check if clearance number exists
     */
    boolean existsByClearanceNumber(String clearanceNumber);
}
