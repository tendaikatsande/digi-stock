package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.Owner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Owner entity operations
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    /**
     * Find owner by national ID
     */
    Optional<Owner> findByNationalId(String nationalId);

    /**
     * Find owner by phone number
     */
    Optional<Owner> findByPhoneNumber(String phoneNumber);

    /**
     * Find owners by district
     */
    List<Owner> findByDistrict(String district);

    /**
     * Find owners by province
     */
    List<Owner> findByProvince(String province);

    /**
     * Find owners with biometric enrollment
     */
    List<Owner> findByBiometricEnrolled(boolean enrolled);

    /**
     * Search owners by name (case-insensitive)
     */
    @Query("SELECT o FROM Owner o WHERE LOWER(o.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Owner> searchByName(String searchTerm);

    /**
     * Check if national ID already exists
     */
    boolean existsByNationalId(String nationalId);
}
