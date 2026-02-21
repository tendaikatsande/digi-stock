package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Vaccination;
import zw.co.digistock.dto.request.CreateVaccinationRequest;
import zw.co.digistock.dto.request.UpdateVaccinationRequest;
import zw.co.digistock.dto.response.VaccinationResponse;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.mapper.VaccinationMapper;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.repository.VaccinationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of IVaccinationService interface.
 * Handles business logic for vaccination records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VaccinationService implements IVaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final LivestockRepository livestockRepository;
    private final OfficerRepository officerRepository;
    private final VaccinationMapper vaccinationMapper;

    @Override
    @Transactional
    public VaccinationResponse createVaccination(CreateVaccinationRequest request) {
        log.info("Creating vaccination record for livestock ID: {}", request.getLivestockId());

        // Validate livestock exists
        Livestock livestock = livestockRepository.findById(request.getLivestockId())
                .orElseThrow(() -> new ResourceNotFoundException("Livestock not found with ID: " + request.getLivestockId()));

        // Validate veterinary officer exists and has correct role
        Officer veterinaryOfficer = officerRepository.findById(request.getVeterinaryOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinary officer not found with ID: " + request.getVeterinaryOfficerId()));

        // Create vaccination entity
        Vaccination vaccination = Vaccination.builder()
                .vaccineType(request.getVaccineType())
                .vaccinationDate(request.getVaccinationDate())
                .batchNumber(request.getBatchNumber())
                .nextVaccinationDate(request.getNextVaccinationDate())
                .notes(request.getNotes())
                .veterinaryOfficer(veterinaryOfficer)
                .livestock(livestock)
                .location(request.getLocation())
                .gpsCoordinates(request.getGpsCoordinates())
                .administrationMethod(request.getAdministrationMethod())
                .manufacturer(request.getManufacturer())
                .lotNumber(request.getLotNumber())
                .vaccineExpiryDate(request.getVaccineExpiryDate())
                .dose(request.getDose())
                .storageTemperature(request.getStorageTemperature())
                .isVerified(false)
                .build();

        Vaccination savedVaccination = vaccinationRepository.save(vaccination);
        log.info("Vaccination record created with ID: {}", savedVaccination.getId());

        return vaccinationMapper.toResponse(savedVaccination);
    }

    @Override
    public VaccinationResponse getVaccinationById(UUID id) {
        log.info("Getting vaccination record with ID: {}", id);
        return vaccinationRepository.findById(id)
                .map(vaccinationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));
    }

    @Override
    @Transactional
    public VaccinationResponse updateVaccination(UUID id, UpdateVaccinationRequest request) {
        log.info("Updating vaccination record with ID: {}", id);

        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));

        // Update fields if provided
        if (request.getVaccineType() != null) {
            vaccination.setVaccineType(request.getVaccineType());
        }
        if (request.getVaccinationDate() != null) {
            vaccination.setVaccinationDate(request.getVaccinationDate());
        }
        if (request.getBatchNumber() != null) {
            vaccination.setBatchNumber(request.getBatchNumber());
        }
        if (request.getNextVaccinationDate() != null) {
            vaccination.setNextVaccinationDate(request.getNextVaccinationDate());
        }
        if (request.getNotes() != null) {
            vaccination.setNotes(request.getNotes());
        }
        if (request.getVeterinaryOfficerId() != null) {
            Officer veterinaryOfficer = officerRepository.findById(request.getVeterinaryOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinary officer not found with ID: " + request.getVeterinaryOfficerId()));
            vaccination.setVeterinaryOfficer(veterinaryOfficer);
        }
        if (request.getLocation() != null) {
            vaccination.setLocation(request.getLocation());
        }
        if (request.getGpsCoordinates() != null) {
            vaccination.setGpsCoordinates(request.getGpsCoordinates());
        }
        if (request.getAdministrationMethod() != null) {
            vaccination.setAdministrationMethod(request.getAdministrationMethod());
        }
        if (request.getManufacturer() != null) {
            vaccination.setManufacturer(request.getManufacturer());
        }
        if (request.getLotNumber() != null) {
            vaccination.setLotNumber(request.getLotNumber());
        }
        if (request.getVaccineExpiryDate() != null) {
            vaccination.setVaccineExpiryDate(request.getVaccineExpiryDate());
        }
        if (request.getDose() != null) {
            vaccination.setDose(request.getDose());
        }
        if (request.getStorageTemperature() != null) {
            vaccination.setStorageTemperature(request.getStorageTemperature());
        }
        if (request.getIsVerified() != null) {
            vaccination.setIsVerified(request.getIsVerified());
        }
        if (request.getVerifiedById() != null) {
            Officer verifiedBy = officerRepository.findById(request.getVerifiedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + request.getVerifiedById()));
            vaccination.setVerifiedBy(verifiedBy);
        }
        if (request.getVerifiedDate() != null) {
            vaccination.setVerifiedDate(request.getVerifiedDate());
        }

        Vaccination updatedVaccination = vaccinationRepository.save(vaccination);
        log.info("Vaccination record updated with ID: {}", updatedVaccination.getId());

        return vaccinationMapper.toResponse(updatedVaccination);
    }

    @Override
    @Transactional
    public void deleteVaccination(UUID id) {
        log.info("Deleting vaccination record with ID: {}", id);
        if (!vaccinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vaccination record not found with ID: " + id);
        }
        vaccinationRepository.deleteById(id);
        log.info("Vaccination record deleted with ID: {}", id);
    }

    @Override
    public Page<VaccinationResponse> getAllVaccinations(Pageable pageable) {
        log.info("Getting all vaccination records with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return vaccinationRepository.findAll(pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByLivestockId(UUID livestockId, Pageable pageable) {
        log.info("Getting vaccination records for livestock ID: {}", livestockId);
        return vaccinationRepository.findByLivestockId(livestockId, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByVeterinaryOfficerId(UUID veterinaryOfficerId, Pageable pageable) {
        log.info("Getting vaccination records for veterinary officer ID: {}", veterinaryOfficerId);
        return vaccinationRepository.findByVeterinaryOfficerId(veterinaryOfficerId, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByVaccineType(String vaccineType, Pageable pageable) {
        log.info("Getting vaccination records for vaccine type: {}", vaccineType);
        return vaccinationRepository.findByVaccineTypeContainingIgnoreCase(vaccineType, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Getting vaccination records between {} and {}", startDate, endDate);
        return vaccinationRepository.findByVaccinationDateBetween(startDate, endDate, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByBatchNumber(String batchNumber, Pageable pageable) {
        log.info("Getting vaccination records for batch number: {}", batchNumber);
        return vaccinationRepository.findByBatchNumber(batchNumber, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getUnverifiedVaccinations(Pageable pageable) {
        log.info("Getting unverified vaccination records");
        return vaccinationRepository.findByIsVerifiedFalse(pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVerifiedVaccinations(Pageable pageable) {
        log.info("Getting verified vaccination records");
        return vaccinationRepository.findByIsVerifiedTrue(pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsDueForBooster(LocalDate dueDate, Pageable pageable) {
        log.info("Getting vaccination records due for booster by {}", dueDate);
        return vaccinationRepository.findByNextVaccinationDateLessThanEqual(dueDate, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByLocation(String location, Pageable pageable) {
        log.info("Getting vaccination records for location: {}", location);
        return vaccinationRepository.findByLocationContainingIgnoreCase(location, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    @Transactional
    public VaccinationResponse verifyVaccination(UUID id, UUID verifiedById) {
        log.info("Verifying vaccination record with ID: {}", id);

        Vaccination vaccination = vaccinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccination record not found with ID: " + id));

        Officer verifiedBy = officerRepository.findById(verifiedById)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with ID: " + verifiedById));

        vaccination.setIsVerified(true);
        vaccination.setVerifiedBy(verifiedBy);
        vaccination.setVerifiedDate(LocalDate.now());

        Vaccination verifiedVaccination = vaccinationRepository.save(vaccination);
        log.info("Vaccination record verified with ID: {}", verifiedVaccination.getId());

        return vaccinationMapper.toResponse(verifiedVaccination);
    }

    @Override
    public List<Object[]> getVaccinationCountByType() {
        log.info("Getting vaccination count by type");
        return vaccinationRepository.countVaccinationsByType();
    }

    @Override
    public List<Object[]> getVaccinationCountByVeterinaryOfficer() {
        log.info("Getting vaccination count by veterinary officer");
        return vaccinationRepository.countVaccinationsByVeterinaryOfficer();
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByDistrict(String district, Pageable pageable) {
        log.info("Getting vaccination records for district: {}", district);
        return vaccinationRepository.findByLivestockOwnerDistrict(district, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public Page<VaccinationResponse> getVaccinationsByProvince(String province, Pageable pageable) {
        log.info("Getting vaccination records for province: {}", province);
        return vaccinationRepository.findByLivestockOwnerProvince(province, pageable)
                .map(vaccinationMapper::toResponse);
    }

    @Override
    public zw.co.digistock.dto.response.VaccinationStatistics getVaccinationStatistics() {
        log.info("Getting vaccination statistics");

        long totalVaccinations = vaccinationRepository.count();
        long verifiedVaccinations = vaccinationRepository.findByIsVerifiedTrue(null).getContent().size();
        long unverifiedVaccinations = vaccinationRepository.findByIsVerifiedFalse(null).getContent().size();
        long dueForBooster = vaccinationRepository.findByNextVaccinationDateLessThanEqual(LocalDate.now(), null).getContent().size();

        return zw.co.digistock.dto.response.VaccinationStatistics.builder()
                .totalVaccinations(totalVaccinations)
                .verifiedVaccinations(verifiedVaccinations)
                .unverifiedVaccinations(unverifiedVaccinations)
                .dueForBooster(dueForBooster)
                .build();
    }
}
