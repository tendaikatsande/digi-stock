package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.config.MinioConfig;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.dto.request.UpdateOfficerRequest;
import zw.co.digistock.dto.response.OfficerResponse;
import zw.co.digistock.exception.DuplicateResourceException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.service.storage.MinioStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for officer management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final MinioStorageService minioStorageService;
    private final MinioConfig minioConfig;

    /**
     * Get all officers (paginated)
     */
    @Transactional(readOnly = true)
    public Page<OfficerResponse> getAllOfficers(Pageable pageable) {
        log.info("Fetching all officers with pagination");
        Page<Officer> page = officerRepository.findAll(pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get officer by ID
     */
    @Transactional(readOnly = true)
    public OfficerResponse getOfficerById(UUID id) {
        log.info("Fetching officer by ID: {}", id);
        Officer officer = officerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", id));
        return mapToResponse(officer);
    }

    /**
     * Get officer by officer code
     */
    @Transactional(readOnly = true)
    public OfficerResponse getOfficerByCode(String officerCode) {
        log.info("Fetching officer by code: {}", officerCode);
        Officer officer = officerRepository.findByOfficerCode(officerCode)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "officerCode", officerCode));
        return mapToResponse(officer);
    }

    /**
     * Get officer by email
     */
    @Transactional(readOnly = true)
    public OfficerResponse getOfficerByEmail(String email) {
        log.info("Fetching officer by email: {}", email);
        Officer officer = officerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "email", email));
        return mapToResponse(officer);
    }

    /**
     * Get officers by role
     */
    @Transactional(readOnly = true)
    public List<OfficerResponse> getOfficersByRole(UserRole role) {
        log.info("Fetching officers by role: {}", role);
        List<Officer> officers = officerRepository.findByRole(role);
        return officers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get officers by district
     */
    @Transactional(readOnly = true)
    public List<OfficerResponse> getOfficersByDistrict(String district) {
        log.info("Fetching officers by district: {}", district);
        List<Officer> officers = officerRepository.findByDistrict(district);
        return officers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get officers by province
     */
    @Transactional(readOnly = true)
    public List<OfficerResponse> getOfficersByProvince(String province) {
        log.info("Fetching officers by province: {}", province);
        List<Officer> officers = officerRepository.findByProvince(province);
        return officers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get active officers
     */
    @Transactional(readOnly = true)
    public List<OfficerResponse> getActiveOfficers() {
        log.info("Fetching active officers");
        List<Officer> officers = officerRepository.findByActive(true);
        return officers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update officer information
     * Note: Role and officer code cannot be changed
     */
    @Transactional
    public OfficerResponse updateOfficer(UUID id, UpdateOfficerRequest request) {
        log.info("Updating officer: {}", id);

        Officer officer = officerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", id));

        // Update fields if provided
        if (request.getFirstName() != null) {
            officer.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            officer.setLastName(request.getLastName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(officer.getEmail())) {
            // Check if new email already exists
            if (officerRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already in use: " + request.getEmail());
            }
            officer.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            officer.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getProvince() != null) {
            officer.setProvince(request.getProvince());
        }

        if (request.getDistrict() != null) {
            officer.setDistrict(request.getDistrict());
        }

        if (request.getActive() != null) {
            officer.setActive(request.getActive());
        }

        Officer updated = officerRepository.save(officer);
        log.info("Successfully updated officer: {}", updated.getOfficerCode());

        return mapToResponse(updated);
    }

    /**
     * Upload officer photo
     */
    @Transactional
    public OfficerResponse uploadPhoto(UUID officerId, MultipartFile file) {
        log.info("Uploading photo for officer: {}", officerId);

        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        // Upload to MinIO
        String folder = "officers/" + officerId.toString();
        String photoRef = minioStorageService.uploadFile(file, "digistock-officers", folder);

        officer.setPhotoRef(photoRef);
        officerRepository.save(officer);

        log.info("Photo uploaded successfully: {}", photoRef);
        return mapToResponse(officer);
    }

    /**
     * Enroll officer fingerprint
     */
    @Transactional
    public OfficerResponse enrollFingerprint(UUID officerId, MultipartFile file) {
        log.info("Enrolling fingerprint for officer: {}", officerId);

        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        // Upload to MinIO
        String folder = "officers/" + officerId.toString() + "/fingerprints";
        String fingerprintRef = minioStorageService.uploadFile(file, "digistock-fingerprints", folder);

        officer.addFingerprint(fingerprintRef);

        // Mark as biometrically enrolled if this is the first fingerprint
        if (!officer.isBiometricEnrolled() && officer.getFingerprintRefs().size() > 0) {
            officer.setBiometricEnrolled(true);
        }

        officerRepository.save(officer);

        log.info("Fingerprint enrolled successfully: {}", fingerprintRef);
        return mapToResponse(officer);
    }

    /**
     * Deactivate officer account
     */
    @Transactional
    public OfficerResponse deactivateOfficer(UUID id) {
        log.info("Deactivating officer: {}", id);

        Officer officer = officerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", id));

        officer.setActive(false);
        Officer updated = officerRepository.save(officer);

        log.info("Officer deactivated: {}", updated.getOfficerCode());
        return mapToResponse(updated);
    }

    /**
     * Reactivate officer account
     */
    @Transactional
    public OfficerResponse reactivateOfficer(UUID id) {
        log.info("Reactivating officer: {}", id);

        Officer officer = officerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", id));

        officer.setActive(true);
        Officer updated = officerRepository.save(officer);

        log.info("Officer reactivated: {}", updated.getOfficerCode());
        return mapToResponse(updated);
    }

    /**
     * Map Officer entity to OfficerResponse DTO
     */
    private OfficerResponse mapToResponse(Officer officer) {
        OfficerResponse.OfficerStats stats = OfficerResponse.OfficerStats.builder()
            .clearancesIssued(officer.getIssuedClearances() != null ? officer.getIssuedClearances().size() : 0)
            .permitsIssued(officer.getIssuedPermits() != null ? officer.getIssuedPermits().size() : 0)
            .build();

        return OfficerResponse.builder()
            .id(officer.getId())
            .officerCode(officer.getOfficerCode())
            .firstName(officer.getFirstName())
            .lastName(officer.getLastName())
            .fullName(officer.getFullName())
            .email(officer.getEmail())
            .phoneNumber(officer.getPhoneNumber())
            .role(officer.getRole())
            .province(officer.getProvince())
            .district(officer.getDistrict())
            .photoRef(officer.getPhotoRef())
            .biometricEnrolled(officer.isBiometricEnrolled())
            .active(officer.isActive())
            .fingerprintCount(officer.getFingerprintRefs() != null ? officer.getFingerprintRefs().size() : 0)
            .createdAt(officer.getCreatedAt())
            .updatedAt(officer.getUpdatedAt())
            .stats(stats)
            .build();
    }
}
