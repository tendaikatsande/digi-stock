package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Officer entity operations
 */
@Repository
public interface OfficerRepository extends JpaRepository<Officer, UUID> {

    /**
     * Find officer by officer code
     */
    Optional<Officer> findByOfficerCode(String officerCode);

    /**
     * Find officer by email
     */
    Optional<Officer> findByEmail(String email);

    /**
     * Find officers by role
     */
    List<Officer> findByRole(UserRole role);

    /**
     * Find officers by district
     */
    List<Officer> findByDistrict(String district);

    /**
     * Find officers by province
     */
    List<Officer> findByProvince(String province);

    /**
     * Find active officers
     */
    List<Officer> findByActive(boolean active);

    /**
     * Find officers by role and district
     */
    List<Officer> findByRoleAndDistrict(UserRole role, String district);

    /**
     * Check if officer code exists
     */
    boolean existsByOfficerCode(String officerCode);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find officer by hashed reset token (SHA-256 hash)
     */
    Optional<Officer> findByResetToken(String hashedToken);
}
