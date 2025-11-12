package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.Province;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Province entity operations
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, UUID> {

    /**
     * Find province by name
     */
    Optional<Province> findByName(String name);

    /**
     * Find province by code
     */
    Optional<Province> findByCode(String code);

    /**
     * Find all active provinces
     */
    List<Province> findByActiveTrue();

    /**
     * Check if province name already exists
     */
    boolean existsByName(String name);

    /**
     * Check if province code already exists
     */
    boolean existsByCode(String code);
}
