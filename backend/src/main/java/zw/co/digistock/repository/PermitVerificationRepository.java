package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.PermitVerification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for PermitVerification entity operations
 */
@Repository
public interface PermitVerificationRepository extends JpaRepository<PermitVerification, UUID> {

    /**
     * Find verifications for a specific permit
     */
    List<PermitVerification> findByPermitIdOrderByVerifiedAtDesc(UUID permitId);

    /**
     * Find verifications by officer
     */
    List<PermitVerification> findByVerifiedById(UUID officerId);

    /**
     * Find verifications within a time range
     */
    List<PermitVerification> findByVerifiedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find flagged (invalid) verifications
     */
    List<PermitVerification> findByValid(boolean valid);

    /**
     * Find recent verifications (last N hours)
     */
    @Query("SELECT v FROM PermitVerification v WHERE v.verifiedAt >= :since ORDER BY v.verifiedAt DESC")
    List<PermitVerification> findRecentVerifications(LocalDateTime since);

    /**
     * Count verifications for a permit
     */
    long countByPermitId(UUID permitId);
}
