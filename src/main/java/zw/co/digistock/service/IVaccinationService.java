package zw.co.digistock.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.digistock.dto.request.CreateVaccinationRequest;
import zw.co.digistock.dto.request.UpdateVaccinationRequest;
import zw.co.digistock.dto.response.VaccinationResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing vaccination records.
 */
public interface IVaccinationService {

    /**
     * Create a new vaccination record.
     *
     * @param request The vaccination details to create
     * @return The created vaccination record
     */
    VaccinationResponse createVaccination(CreateVaccinationRequest request);

    /**
     * Get a vaccination record by ID.
     *
     * @param id The ID of the vaccination record
     * @return The vaccination record
     */
    VaccinationResponse getVaccinationById(UUID id);

    /**
     * Update an existing vaccination record.
     *
     * @param id The ID of the vaccination record to update
     * @param request The updated vaccination details
     * @return The updated vaccination record
     */
    VaccinationResponse updateVaccination(UUID id, UpdateVaccinationRequest request);

    /**
     * Delete a vaccination record.
     *
     * @param id The ID of the vaccination record to delete
     */
    void deleteVaccination(UUID id);

    /**
     * Get all vaccination records.
     *
     * @param pageable Pagination information
     * @return Page of vaccination records
     */
    Page<VaccinationResponse> getAllVaccinations(Pageable pageable);

    /**
     * Get all vaccination records for a specific livestock animal.
     *
     * @param livestockId The ID of the livestock animal
     * @param pageable Pagination information
     * @return Page of vaccination records for the specified livestock
     */
    Page<VaccinationResponse> getVaccinationsByLivestockId(UUID livestockId, Pageable pageable);

    /**
     * Get all vaccination records administered by a specific veterinary officer.
     *
     * @param veterinaryOfficerId The ID of the veterinary officer
     * @param pageable Pagination information
     * @return Page of vaccination records administered by the specified officer
     */
    Page<VaccinationResponse> getVaccinationsByVeterinaryOfficerId(UUID veterinaryOfficerId, Pageable pageable);

    /**
     * Get all vaccination records of a specific type.
     *
     * @param vaccineType The type of vaccine to search for
     * @param pageable Pagination information
     * @return Page of vaccination records of the specified type
     */
    Page<VaccinationResponse> getVaccinationsByVaccineType(String vaccineType, Pageable pageable);

    /**
     * Get all vaccination records administered within a specific date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @param pageable Pagination information
     * @return Page of vaccination records within the date range
     */
    Page<VaccinationResponse> getVaccinationsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Get all vaccination records with a specific batch number.
     *
     * @param batchNumber The batch number to search for
     * @param pageable Pagination information
     * @return Page of vaccination records with the specified batch number
     */
    Page<VaccinationResponse> getVaccinationsByBatchNumber(String batchNumber, Pageable pageable);

    /**
     * Get all unverified vaccination records.
     *
     * @param pageable Pagination information
     * @return Page of unverified vaccination records
     */
    Page<VaccinationResponse> getUnverifiedVaccinations(Pageable pageable);

    /**
     * Get all verified vaccination records.
     *
     * @param pageable Pagination information
     * @return Page of verified vaccination records
     */
    Page<VaccinationResponse> getVerifiedVaccinations(Pageable pageable);

    /**
     * Get all vaccination records due for booster shots by a specific date.
     *
     * @param dueDate The due date for booster shots
     * @param pageable Pagination information
     * @return Page of vaccination records due for booster shots
     */
    Page<VaccinationResponse> getVaccinationsDueForBooster(LocalDate dueDate, Pageable pageable);

    /**
     * Get all vaccination records administered in a specific location.
     *
     * @param location The location to search for
     * @param pageable Pagination information
     * @return Page of vaccination records administered in the specified location
     */
    Page<VaccinationResponse> getVaccinationsByLocation(String location, Pageable pageable);

    /**
     * Verify a vaccination record.
     *
     * @param id The ID of the vaccination record to verify
     * @param verifiedById The ID of the officer who verified the record
     * @return The verified vaccination record
     */
    VaccinationResponse verifyVaccination(UUID id, UUID verifiedById);

    /**
     * Get vaccination counts by vaccine type.
     *
     * @return List of Object arrays containing vaccine type and count
     */
    List<Object[]> getVaccinationCountByType();

    /**
     * Get vaccination counts by veterinary officer.
     *
     * @return List of Object arrays containing officer ID and count
     */
    List<Object[]> getVaccinationCountByVeterinaryOfficer();

    /**
     * Get vaccination records for livestock in a specific district.
     *
     * @param district The district to search for
     * @param pageable Pagination information
     * @return Page of vaccination records for livestock in the specified district
     */
    Page<VaccinationResponse> getVaccinationsByDistrict(String district, Pageable pageable);

    /**
     * Get vaccination records for livestock in a specific province.
     *
     * @param province The province to search for
     * @param pageable Pagination information
     * @return Page of vaccination records for livestock in the specified province
     */
    Page<VaccinationResponse> getVaccinationsByProvince(String province, Pageable pageable);

    /**
     * Get statistics about vaccination records.
     *
     * @return Object containing vaccination statistics
     */
    zw.co.digistock.dto.response.VaccinationStatistics getVaccinationStatistics();
}
