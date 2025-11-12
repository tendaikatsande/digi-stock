package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.config.MinioConfig;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.LivestockPhoto;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.dto.request.RegisterLivestockRequest;
import zw.co.digistock.dto.request.UpdateLivestockRequest;
import zw.co.digistock.dto.response.LivestockResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.DuplicateResourceException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.LivestockPhotoRepository;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.service.qr.QrCodeService;
import zw.co.digistock.service.storage.MinioStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for livestock management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LivestockService {

    private final LivestockRepository livestockRepository;
    private final OwnerRepository ownerRepository;
    private final LivestockPhotoRepository photoRepository;
    private final MinioStorageService minioStorageService;
    private final QrCodeService qrCodeService;
    private final MinioConfig minioConfig;

    /**
     * Register new livestock
     */
    @Transactional
    public LivestockResponse registerLivestock(RegisterLivestockRequest request) {
        log.info("Registering new livestock with tag code: {}", request.getTagCode());

        // Check if tag code already exists
        if (livestockRepository.existsByTagCode(request.getTagCode())) {
            throw new DuplicateResourceException("Livestock", "tagCode", request.getTagCode());
        }

        // Verify owner exists
        Owner owner = ownerRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", request.getOwnerId()));

        // Build livestock entity
        Livestock livestock = Livestock.builder()
            .tagCode(request.getTagCode())
            .name(request.getName())
            .breed(request.getBreed())
            .sex(request.getSex())
            .birthDate(request.getBirthDate())
            .color(request.getColor())
            .distinguishingMarks(request.getDistinguishingMarks())
            .owner(owner)
            .registrationLatitude(request.getRegistrationLatitude())
            .registrationLongitude(request.getRegistrationLongitude())
            .stolen(false)
            .build();

        // Set parentage if provided
        if (request.getMotherId() != null) {
            Livestock mother = livestockRepository.findById(request.getMotherId())
                .orElseThrow(() -> new ResourceNotFoundException("Livestock (mother)", "id", request.getMotherId()));
            livestock.setMother(mother);
        }

        if (request.getFatherId() != null) {
            Livestock father = livestockRepository.findById(request.getFatherId())
                .orElseThrow(() -> new ResourceNotFoundException("Livestock (father)", "id", request.getFatherId()));
            livestock.setFather(father);
        }

        // Save livestock
        Livestock saved = livestockRepository.save(livestock);
        log.info("Successfully registered livestock: {}", saved.getTagCode());

        return mapToResponse(saved);
    }

    /**
     * Upload photo for livestock
     */
    @Transactional
    public LivestockResponse uploadPhoto(UUID livestockId, MultipartFile file, String description, String photoType) {
        log.info("Uploading photo for livestock: {}", livestockId);

        Livestock livestock = livestockRepository.findById(livestockId)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", livestockId));

        // Upload to MinIO
        String folder = "livestock/" + livestockId.toString();
        String photoRef = minioStorageService.uploadFile(file, minioConfig.getLivestockPhotosBucket(), folder);

        // Create photo record
        LivestockPhoto photo = LivestockPhoto.builder()
            .livestock(livestock)
            .photoRef(photoRef)
            .description(description)
            .photoType(photoType != null ? photoType : "GENERAL")
            .build();

        photoRepository.save(photo);
        log.info("Photo uploaded successfully: {}", photoRef);

        // Reload livestock with photos
        livestock = livestockRepository.findById(livestockId).orElseThrow();
        return mapToResponse(livestock);
    }

    /**
     * Get livestock by ID
     */
    @Transactional(readOnly = true)
    public LivestockResponse getLivestockById(UUID id) {
        Livestock livestock = livestockRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", id));
        return mapToResponse(livestock);
    }

    /**
     * Get livestock by tag code
     */
    @Transactional(readOnly = true)
    public LivestockResponse getLivestockByTagCode(String tagCode) {
        Livestock livestock = livestockRepository.findByTagCode(tagCode)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "tagCode", tagCode));
        return mapToResponse(livestock);
    }

    /**
     * Get all livestock (paginated)
     */
    @Transactional(readOnly = true)
    public Page<LivestockResponse> getAllLivestock(Pageable pageable) {
        Page<Livestock> page = livestockRepository.findAll(pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get all livestock for an owner (paginated)
     */
    @Transactional(readOnly = true)
    public Page<LivestockResponse> getLivestockByOwner(UUID ownerId, Pageable pageable) {
        Page<Livestock> page = livestockRepository.findByOwnerId(ownerId, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get offspring by mother (paginated)
     */
    @Transactional(readOnly = true)
    public Page<LivestockResponse> getOffspringByMotherId(UUID livestockId, Pageable pageable) {
        Page<Livestock> page = livestockRepository.findByMotherId(livestockId, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get offspring by father (paginated)
     */
    @Transactional(readOnly = true)
    public Page<LivestockResponse> getOffspringByFatherId(UUID livestockId, Pageable pageable) {
        Page<Livestock> page = livestockRepository.findByFatherId(livestockId, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Mark livestock as stolen
     */
    @Transactional
    public LivestockResponse markAsStolen(UUID livestockId) {
        log.warn("Marking livestock as stolen: {}", livestockId);

        Livestock livestock = livestockRepository.findById(livestockId)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", livestockId));

        livestock.setStolen(true);
        livestock.setStolenDate(java.time.LocalDate.now());

        Livestock updated = livestockRepository.save(livestock);
        log.info("Livestock marked as stolen: {}", updated.getTagCode());

        return mapToResponse(updated);
    }

    /**
     * Mark livestock as recovered
     */
    @Transactional
    public LivestockResponse markAsRecovered(UUID livestockId) {
        log.info("Marking livestock as recovered: {}", livestockId);

        Livestock livestock = livestockRepository.findById(livestockId)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", livestockId));

        livestock.setStolen(false);
        livestock.setStolenDate(null);

        Livestock updated = livestockRepository.save(livestock);
        log.info("Livestock marked as recovered: {}", updated.getTagCode());

        return mapToResponse(updated);
    }

    /**
     * Get stolen livestock (paginated)
     */
    @Transactional(readOnly = true)
    public Page<LivestockResponse> getStolenLivestock(Pageable pageable) {
        Page<Livestock> page = livestockRepository.findByStolen(true, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Update livestock information (limited fields)
     * Only descriptive fields can be updated. Tag code, owner, and parentage are immutable.
     */
    @Transactional
    public LivestockResponse updateLivestock(UUID livestockId, UpdateLivestockRequest request) {
        log.info("Updating livestock: {}", livestockId);

        Livestock livestock = livestockRepository.findById(livestockId)
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", livestockId));

        // Update fields if provided
        if (request.getName() != null) {
            livestock.setName(request.getName());
        }

        if (request.getBreed() != null) {
            livestock.setBreed(request.getBreed());
        }

        if (request.getSex() != null) {
            livestock.setSex(request.getSex());
        }

        if (request.getBirthDate() != null) {
            livestock.setBirthDate(request.getBirthDate());
        }

        if (request.getColor() != null) {
            livestock.setColor(request.getColor());
        }

        if (request.getDistinguishingMarks() != null) {
            livestock.setDistinguishingMarks(request.getDistinguishingMarks());
        }

        Livestock updated = livestockRepository.save(livestock);
        log.info("Successfully updated livestock: {}", updated.getTagCode());

        return mapToResponse(updated);
    }

    /**
     * Map entity to response DTO
     */
    private LivestockResponse mapToResponse(Livestock livestock) {
        LivestockResponse.OwnerSummary ownerSummary = LivestockResponse.OwnerSummary.builder()
            .id(livestock.getOwner().getId())
            .nationalId(livestock.getOwner().getNationalId())
            .fullName(livestock.getOwner().getFullName())
            .phoneNumber(livestock.getOwner().getPhoneNumber())
            .district(livestock.getOwner().getDistrict())
            .province(livestock.getOwner().getProvince())
            .build();

        LivestockResponse.LivestockSummary motherSummary = null;
        if (livestock.getMother() != null) {
            motherSummary = LivestockResponse.LivestockSummary.builder()
                .id(livestock.getMother().getId())
                .tagCode(livestock.getMother().getTagCode())
                .name(livestock.getMother().getName())
                .breed(livestock.getMother().getBreed())
                .build();
        }

        LivestockResponse.LivestockSummary fatherSummary = null;
        if (livestock.getFather() != null) {
            fatherSummary = LivestockResponse.LivestockSummary.builder()
                .id(livestock.getFather().getId())
                .tagCode(livestock.getFather().getTagCode())
                .name(livestock.getFather().getName())
                .breed(livestock.getFather().getBreed())
                .build();
        }

        List<LivestockResponse.PhotoInfo> photos = livestock.getPhotos().stream()
            .map(photo -> LivestockResponse.PhotoInfo.builder()
                .id(photo.getId())
                .photoRef(photo.getPhotoRef())
                .description(photo.getDescription())
                .photoType(photo.getPhotoType())
                .build())
            .collect(Collectors.toList());

        return LivestockResponse.builder()
            .id(livestock.getId())
            .tagCode(livestock.getTagCode())
            .name(livestock.getName())
            .breed(livestock.getBreed())
            .sex(livestock.getSex())
            .birthDate(livestock.getBirthDate())
            .color(livestock.getColor())
            .distinguishingMarks(livestock.getDistinguishingMarks())
            .owner(ownerSummary)
            .mother(motherSummary)
            .father(fatherSummary)
            .registrationLatitude(livestock.getRegistrationLatitude())
            .registrationLongitude(livestock.getRegistrationLongitude())
            .stolen(livestock.isStolen())
            .stolenDate(livestock.getStolenDate())
            .photos(photos)
            .createdAt(livestock.getCreatedAt())
            .updatedAt(livestock.getUpdatedAt())
            .build();
    }
}
