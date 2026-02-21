package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.Vaccination;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Vaccination entity.
 * Provides methods for accessing and querying vaccination records.
 */
@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, UUID> {

    /**
     * Find all vaccinations for a specific livestock animal.
     *
     * @param livestockId ID of the livestock animal
     * @param pageable Pagination information
     * @return Page of vaccinations for the specified livestock
     */
    Page<Vaccination> findByLivestockId(UUID livestockId, Pageable pageable);

    /**
     * Find all vaccinations administered by a specific veterinary officer.
     *
     * @param veterinaryOfficerId ID of the veterinary officer
     * @param pageable Pagination information
     * @return Page of vaccinations administered by the specified officer
     */
    Page<Vaccination> findByVeterinaryOfficerId(UUID veterinaryOfficerId, Pageable pageable);

    /**
     * Find all vaccinations of a specific type.
     *
     * @param vaccineType Type of vaccine
     * @param pageable Pagination information
     * @return Page of vaccinations of the specified type
     */
    Page<Vaccination> findByVaccineTypeContainingIgnoreCase(String vaccineType, Pageable pageable);

    /**
     * Find all vaccinations administered on or after a specific date.
     *
     * @param date The start date for the query
     * @param pageable Pagination information
     * @return Page of vaccinations administered on or after the specified date
     */
    Page<Vaccination> findByVaccinationDateGreaterThanEqual(LocalDate date, Pageable pageable);

    /**
     * Find all vaccinations administered on or before a specific date.
     *
     * @param date The end date for the query
     * @param pageable Pagination information
     * @return Page of vaccinations administered on or before the specified date
     */
    Page<Vaccination> findByVaccinationDateLessThanEqual(LocalDate date, Pageable pageable);

    /**
     * Find all vaccinations administered within a specific date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @param pageable Pagination information
     * @return Page of vaccinations administered within the date range
     */
    @Query("SELECT v FROM Vaccination v WHERE v.vaccinationDate BETWEEN :startDate AND :endDate")
    Page<Vaccination> findByVaccinationDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find all vaccinations with a specific batch number.
     *
     * @param batchNumber Batch number to search for
     * @param pageable Pagination information
     * @return Page of vaccinations with the specified batch number
     */
    Page<Vaccination> findByBatchNumber(String batchNumber, Pageable pageable);

    /**
     * Find all unverified vaccinations.
     *
     * @param pageable Pagination information
     * @return Page of unverified vaccinations
     */
    Page<Vaccination> findByIsVerifiedFalse(Pageable pageable);

    /**
     * Find all verified vaccinations.
     *
     * @param pageable Pagination information
     * @return Page of verified vaccinations
     */
    Page<Vaccination> findByIsVerifiedTrue(Pageable pageable);

    /**
     * Find all vaccinations due for booster shots by a specific date.
     *
     * @param date The due date
     * @param pageable Pagination information
     * @return Page of vaccinations due for booster shots
     */
    Page<Vaccination> findByNextVaccinationDateLessThanEqual(LocalDate date, Pageable pageable);

    /**
     * Find all vaccinations administered in a specific location.
     *
     * @param location The location to search for
     * @param pageable Pagination information
     * @return Page of vaccinations administered in the specified location
     */
    Page<Vaccination> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    /**
     * Count the number of vaccinations per vaccine type.
     *
     * @return List of Object arrays containing vaccine type and count
     */
    @Query("SELECT v.vaccineType, COUNT(v) FROM Vaccination v GROUP BY v.vaccineType")
    List<Object[]> countVaccinationsByType();

    /**
     * Count the number of vaccinations administered per veterinary officer.
     *
     * @return List of Object arrays containing officer ID and count
     */
    @Query("SELECT v.veterinaryOfficer.id, COUNT(v) FROM Vaccination v GROUP BY v.veterinaryOfficer.id")
    List<Object[]> countVaccinationsByVeterinaryOfficer();

    /**
     * Find all vaccinations for livestock in a specific district.
     *
     * @param district The district to search for
     * @param pageable Pagination information
     * @return Page of vaccinations for livestock in the specified district
     */
    @Query("SELECT v FROM Vaccination v WHERE v.livestock.owner.district = :district")
    Page<Vaccination> findByLivestockOwnerDistrict(@Param("district") String district, Pageable pageable);

    /**
     * Find all vaccinations for livestock in a specific province.
     *
     * @param province The province to search for
     * @param pageable Pagination information
     * @return Page of vaccinations for livestock in the specified province
     */
    @Query("SELECT v FROM Vaccination v WHERE v.livestock.owner.province = :province")
    Page<Vaccination> findByLivestockOwnerProvince(@Param("province") String province, Pageable pageable);
}
