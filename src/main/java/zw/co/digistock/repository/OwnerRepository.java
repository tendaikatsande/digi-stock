package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find owners by district (paginated)
     */
    Page<Owner> findByDistrict(String district, Pageable pageable);

    /**
     * Find owners by province (paginated)
     */
    Page<Owner> findByProvince(String province, Pageable pageable);

    /**
     * Find owners with biometric enrollment (paginated)
     */
    Page<Owner> findByBiometricEnrolled(boolean enrolled, Pageable pageable);

    /**
     * Search owners by name (case-insensitive) - paginated
     */
    @Query("SELECT o FROM Owner o WHERE LOWER(o.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(o.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Owner> searchByName(String searchTerm, Pageable pageable);

    /**
     * Check if national ID already exists
     */
    boolean existsByNationalId(String nationalId);
}
