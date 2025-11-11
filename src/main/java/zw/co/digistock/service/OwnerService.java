package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.config.MinioConfig;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.dto.request.RegisterOwnerRequest;
import zw.co.digistock.dto.response.OwnerResponse;
import zw.co.digistock.exception.DuplicateResourceException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.service.biometric.BiometricService;
import zw.co.digistock.service.storage.MinioStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for owner management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final LivestockRepository livestockRepository;
    private final MinioStorageService minioStorageService;
    private final BiometricService biometricService;
    private final MinioConfig minioConfig;

    /**
     * Register new owner
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "owners", allEntries = true),
        @CacheEvict(value = "ownerPages", allEntries = true)
    })
    public OwnerResponse registerOwner(RegisterOwnerRequest request) {
        log.info("Registering new owner: {} {}", request.getFirstName(), request.getLastName());

        // Check if national ID already exists
        if (ownerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("Owner", "nationalId", request.getNationalId());
        }

        Owner owner = Owner.builder()
            .nationalId(request.getNationalId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .address(request.getAddress())
            .ward(request.getWard())
            .district(request.getDistrict())
            .province(request.getProvince())
            .biometricEnrolled(false)
            .build();

        Owner saved = ownerRepository.save(owner);
        log.info("Owner registered successfully: {}", saved.getNationalId());

        return mapToResponse(saved);
    }

    /**
     * Enroll owner fingerprint
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "owners", key = "#ownerId"),
        @CacheEvict(value = "ownerPages", allEntries = true)
    })
    public OwnerResponse enrollFingerprint(UUID ownerId, MultipartFile fingerprintImage) {
        log.info("Enrolling fingerprint for owner: {}", ownerId);

        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        try {
            // Extract fingerprint template
            byte[] imageBytes = fingerprintImage.getBytes();
            byte[] template = biometricService.extractTemplate(imageBytes);

            // Upload template to MinIO
            String folder = "owners/" + ownerId.toString();
            String templateRef = minioStorageService.uploadBytes(
                template,
                minioConfig.getFingerprintsBucket(),
                folder + "/fingerprint_" + System.currentTimeMillis() + ".fpt",
                "application/octet-stream"
            );

            owner.addFingerprint(templateRef);
            owner.setBiometricEnrolled(true);

            Owner updated = ownerRepository.save(owner);
            log.info("Fingerprint enrolled successfully for owner: {}", ownerId);

            return mapToResponse(updated);
        } catch (Exception e) {
            log.error("Failed to enroll fingerprint", e);
            throw new RuntimeException("Fingerprint enrollment failed", e);
        }
    }

    /**
     * Upload owner photo
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "owners", key = "#ownerId"),
        @CacheEvict(value = "ownerPages", allEntries = true)
    })
    public OwnerResponse uploadPhoto(UUID ownerId, MultipartFile photo) {
        log.info("Uploading photo for owner: {}", ownerId);

        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        String folder = "owners/" + ownerId.toString();
        String photoRef = minioStorageService.uploadFile(
            photo,
            minioConfig.getLivestockPhotosBucket(),
            folder
        );

        owner.setPhotoRef(photoRef);
        Owner updated = ownerRepository.save(owner);
        log.info("Photo uploaded successfully: {}", photoRef);

        return mapToResponse(updated);
    }

    /**
     * Get owner by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "owners", key = "#id")
    public OwnerResponse getOwnerById(UUID id) {
        Owner owner = ownerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", id));
        return mapToResponse(owner);
    }

    /**
     * Get owner by national ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "owners", key = "'nationalId:' + #nationalId")
    public OwnerResponse getOwnerByNationalId(String nationalId) {
        Owner owner = ownerRepository.findByNationalId(nationalId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "nationalId", nationalId));
        return mapToResponse(owner);
    }

    /**
     * Get owners by district (paginated)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ownerPages", key = "'district:' + #district + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<OwnerResponse> getOwnersByDistrict(String district, Pageable pageable) {
        Page<Owner> page = ownerRepository.findByDistrict(district, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Search owners by name (paginated)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ownerPages", key = "'search:' + #searchTerm + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<OwnerResponse> searchOwnersByName(String searchTerm, Pageable pageable) {
        Page<Owner> page = ownerRepository.searchByName(searchTerm, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get all owners (paginated)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ownerPages", key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<OwnerResponse> getAllOwners(Pageable pageable) {
        Page<Owner> page = ownerRepository.findAll(pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Update owner
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "owners", key = "#id"),
        @CacheEvict(value = "ownerPages", allEntries = true)
    })
    public OwnerResponse updateOwner(UUID id, RegisterOwnerRequest request) {
        log.info("Updating owner: {}", id);

        Owner owner = ownerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", id));

        // Check if changing national ID to one that already exists
        if (!owner.getNationalId().equals(request.getNationalId()) &&
            ownerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("Owner", "nationalId", request.getNationalId());
        }

        owner.setNationalId(request.getNationalId());
        owner.setFirstName(request.getFirstName());
        owner.setLastName(request.getLastName());
        owner.setPhoneNumber(request.getPhoneNumber());
        owner.setEmail(request.getEmail());
        owner.setAddress(request.getAddress());
        owner.setWard(request.getWard());
        owner.setDistrict(request.getDistrict());
        owner.setProvince(request.getProvince());

        Owner updated = ownerRepository.save(owner);
        log.info("Owner updated successfully: {}", id);

        return mapToResponse(updated);
    }

    /**
     * Map entity to response DTO
     */
    private OwnerResponse mapToResponse(Owner owner) {
        long livestockCount = livestockRepository.countByOwnerId(owner.getId());

        return OwnerResponse.builder()
            .id(owner.getId())
            .nationalId(owner.getNationalId())
            .firstName(owner.getFirstName())
            .lastName(owner.getLastName())
            .fullName(owner.getFullName())
            .phoneNumber(owner.getPhoneNumber())
            .email(owner.getEmail())
            .address(owner.getAddress())
            .ward(owner.getWard())
            .district(owner.getDistrict())
            .province(owner.getProvince())
            .photoRef(owner.getPhotoRef())
            .biometricEnrolled(owner.isBiometricEnrolled())
            .livestockCount((int) livestockCount)
            .createdAt(owner.getCreatedAt())
            .updatedAt(owner.getUpdatedAt())
            .build();
    }
}
