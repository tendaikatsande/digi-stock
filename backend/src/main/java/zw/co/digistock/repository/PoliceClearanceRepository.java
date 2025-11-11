package zw.co.digistock.repository;

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
     * Find clearances for a specific livestock
     */
    List<PoliceClearance> findByLivestockId(UUID livestockId);

    /**
     * Find clearances by owner
     */
    List<PoliceClearance> findByOwnerId(UUID ownerId);

    /**
     * Find clearances by status
     */
    List<PoliceClearance> findByStatus(ClearanceStatus status);

    /**
     * Find clearances issued by a specific officer
     */
    List<PoliceClearance> findByIssuedById(UUID officerId);

    /**
     * Find valid (approved and not expired) clearances for livestock
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.livestock.id = :livestockId " +
           "AND c.status = 'APPROVED' AND c.expiryDate >= :today ORDER BY c.clearanceDate DESC")
    List<PoliceClearance> findValidClearancesForLivestock(UUID livestockId, LocalDate today);

    /**
     * Find most recent clearance for livestock
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.livestock.id = :livestockId " +
           "ORDER BY c.clearanceDate DESC LIMIT 1")
    Optional<PoliceClearance> findLatestClearanceForLivestock(UUID livestockId);

    /**
     * Find expired clearances
     */
    @Query("SELECT c FROM PoliceClearance c WHERE c.status = 'APPROVED' AND c.expiryDate < :today")
    List<PoliceClearance> findExpiredClearances(LocalDate today);

    /**
     * Find pending clearances
     */
    List<PoliceClearance> findByStatusOrderByClearanceDateDesc(ClearanceStatus status);

    /**
     * Check if clearance number exists
     */
    boolean existsByClearanceNumber(String clearanceNumber);
}
