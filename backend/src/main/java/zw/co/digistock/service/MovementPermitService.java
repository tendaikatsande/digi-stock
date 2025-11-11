package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.*;
import zw.co.digistock.domain.enums.ClearanceStatus;
import zw.co.digistock.domain.enums.PermitStatus;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.dto.request.CreatePermitRequest;
import zw.co.digistock.dto.response.PermitResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.*;
import zw.co.digistock.service.qr.QrCodeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for movement permit operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovementPermitService {

    private final MovementPermitRepository permitRepository;
    private final PoliceClearanceRepository clearanceRepository;
    private final LivestockRepository livestockRepository;
    private final OfficerRepository officerRepository;
    private final PermitVerificationRepository verificationRepository;
    private final QrCodeService qrCodeService;

    @Value("${digistock.permit.default-validity-days:7}")
    private int defaultValidityDays;

    /**
     * Create movement permit
     */
    @Transactional
    public PermitResponse createPermit(CreatePermitRequest request, UUID officerId) {
        log.info("Creating movement permit for livestock: {}", request.getLivestockId());

        // Verify officer is AGRITEX or ADMIN
        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        if (officer.getRole() != UserRole.AGRITEX_OFFICER && officer.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Only AGRITEX officers can issue movement permits");
        }

        // Verify clearance exists and is valid
        PoliceClearance clearance = clearanceRepository.findById(request.getClearanceId())
            .orElseThrow(() -> new ResourceNotFoundException("Police Clearance", "id", request.getClearanceId()));

        if (clearance.getStatus() != ClearanceStatus.APPROVED) {
            throw new BusinessException("Clearance must be APPROVED. Current status: " + clearance.getStatus());
        }

        if (!clearance.isValid()) {
            throw new BusinessException("Clearance has expired. Expiry date: " + clearance.getExpiryDate());
        }

        // Verify livestock
        Livestock livestock = livestockRepository.findById(request.getLivestockId())
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", request.getLivestockId()));

        if (livestock.isStolen()) {
            throw new BusinessException("Cannot issue permit for stolen livestock: " + livestock.getTagCode());
        }

        // Verify clearance is for this livestock
        if (!clearance.getLivestock().getId().equals(livestock.getId())) {
            throw new BusinessException("Clearance is not for this livestock");
        }

        // Validate dates
        if (request.getValidUntil().isBefore(request.getValidFrom())) {
            throw new BusinessException("Valid until date must be after valid from date");
        }

        // Generate permit number
        String permitNumber = generatePermitNumber();

        // Create permit
        MovementPermit permit = MovementPermit.builder()
            .permitNumber(permitNumber)
            .clearance(clearance)
            .livestock(livestock)
            .fromLocation(request.getFromLocation())
            .toLocation(request.getToLocation())
            .purpose(request.getPurpose())
            .transportMode(request.getTransportMode())
            .vehicleNumber(request.getVehicleNumber())
            .driverName(request.getDriverName())
            .issuedBy(officer)
            .issuedAt(LocalDateTime.now())
            .validFrom(request.getValidFrom())
            .validUntil(request.getValidUntil())
            .status(PermitStatus.APPROVED)
            .issueLatitude(request.getIssueLatitude())
            .issueLongitude(request.getIssueLongitude())
            .build();

        // Generate QR code
        String qrRef = qrCodeService.generatePermitQrCode(
            permit.getPermitNumber(),
            livestock.getTagCode(),
            permit.getValidUntil().toString(),
            UUID.randomUUID().toString()
        );
        permit.setQrRef(qrRef);

        MovementPermit saved = permitRepository.save(permit);
        log.info("Movement permit created: {}", permitNumber);

        return mapToResponse(saved);
    }

    /**
     * Verify permit at checkpoint
     */
    @Transactional
    public PermitResponse verifyPermit(UUID permitId, UUID officerId, Double latitude, Double longitude, String notes) {
        log.info("Verifying permit: {} by officer: {}", permitId, officerId);

        MovementPermit permit = permitRepository.findById(permitId)
            .orElseThrow(() -> new ResourceNotFoundException("Movement Permit", "id", permitId));

        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        // Check permit validity
        boolean isValid = permit.isValid();
        String flagReason = null;

        if (!isValid) {
            if (permit.getStatus() != PermitStatus.APPROVED) {
                flagReason = "Permit status is " + permit.getStatus();
            } else if (LocalDate.now().isBefore(permit.getValidFrom())) {
                flagReason = "Permit not yet valid. Valid from: " + permit.getValidFrom();
            } else if (LocalDate.now().isAfter(permit.getValidUntil())) {
                flagReason = "Permit has expired. Valid until: " + permit.getValidUntil();
            }
        }

        // Check if livestock is stolen
        if (permit.getLivestock().isStolen()) {
            isValid = false;
            flagReason = "ALERT: Livestock is reported stolen!";
            log.warn("Stolen livestock detected during verification: {}", permit.getLivestock().getTagCode());
        }

        // Create verification record
        PermitVerification verification = PermitVerification.builder()
            .permit(permit)
            .verifiedBy(officer)
            .verifiedAt(LocalDateTime.now())
            .verificationLatitude(latitude)
            .verificationLongitude(longitude)
            .notes(notes)
            .valid(isValid)
            .flagReason(flagReason)
            .build();

        verificationRepository.save(verification);

        // Update permit status if first verification
        if (permit.getStatus() == PermitStatus.APPROVED) {
            permit.setStatus(PermitStatus.IN_TRANSIT);
            permitRepository.save(permit);
        }

        log.info("Permit verification recorded. Valid: {}", isValid);
        return mapToResponse(permit);
    }

    /**
     * Complete movement permit
     */
    @Transactional
    public PermitResponse completePermit(UUID permitId, Double latitude, Double longitude) {
        log.info("Completing movement permit: {}", permitId);

        MovementPermit permit = permitRepository.findById(permitId)
            .orElseThrow(() -> new ResourceNotFoundException("Movement Permit", "id", permitId));

        if (permit.getStatus() != PermitStatus.IN_TRANSIT && permit.getStatus() != PermitStatus.APPROVED) {
            throw new BusinessException("Cannot complete permit with status: " + permit.getStatus());
        }

        permit.setStatus(PermitStatus.COMPLETED);
        permit.setCompletedAt(LocalDateTime.now());
        permit.setCompletionLatitude(latitude);
        permit.setCompletionLongitude(longitude);

        MovementPermit updated = permitRepository.save(permit);
        log.info("Movement permit completed: {}", permit.getPermitNumber());

        return mapToResponse(updated);
    }

    /**
     * Cancel permit
     */
    @Transactional
    public PermitResponse cancelPermit(UUID permitId, String reason) {
        log.info("Cancelling permit: {}", permitId);

        MovementPermit permit = permitRepository.findById(permitId)
            .orElseThrow(() -> new ResourceNotFoundException("Movement Permit", "id", permitId));

        if (permit.getStatus() == PermitStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel completed permit");
        }

        permit.setStatus(PermitStatus.CANCELLED);

        MovementPermit updated = permitRepository.save(permit);
        log.info("Permit cancelled: {}", permit.getPermitNumber());

        return mapToResponse(updated);
    }

    /**
     * Get permit by ID
     */
    @Transactional(readOnly = true)
    public PermitResponse getPermitById(UUID id) {
        MovementPermit permit = permitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movement Permit", "id", id));
        return mapToResponse(permit);
    }

    /**
     * Get permit by permit number
     */
    @Transactional(readOnly = true)
    public PermitResponse getPermitByNumber(String permitNumber) {
        MovementPermit permit = permitRepository.findByPermitNumber(permitNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Movement Permit", "permitNumber", permitNumber));
        return mapToResponse(permit);
    }

    /**
     * Get permits for livestock
     */
    @Transactional(readOnly = true)
    public List<PermitResponse> getPermitsByLivestock(UUID livestockId) {
        return permitRepository.findByLivestockId(livestockId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get permits by status
     */
    @Transactional(readOnly = true)
    public List<PermitResponse> getPermitsByStatus(PermitStatus status) {
        return permitRepository.findByStatusOrderByIssuedAtDesc(status).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get valid permits
     */
    @Transactional(readOnly = true)
    public List<PermitResponse> getValidPermits() {
        return permitRepository.findValidPermits(LocalDate.now()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Generate permit number
     * Format: DG-{YEAR}-{SEQUENTIAL}
     */
    private String generatePermitNumber() {
        int year = LocalDate.now().getYear();
        long count = permitRepository.count() + 1;
        return String.format("DG-%d-%06d", year, count);
    }

    /**
     * Map entity to response DTO
     */
    private PermitResponse mapToResponse(MovementPermit permit) {
        int verificationCount = (int) verificationRepository.countByPermitId(permit.getId());

        return PermitResponse.builder()
            .id(permit.getId())
            .permitNumber(permit.getPermitNumber())
            .status(permit.getStatus())
            .valid(permit.isValid())
            .fromLocation(permit.getFromLocation())
            .toLocation(permit.getToLocation())
            .purpose(permit.getPurpose())
            .transportMode(permit.getTransportMode())
            .vehicleNumber(permit.getVehicleNumber())
            .driverName(permit.getDriverName())
            .issuedAt(permit.getIssuedAt())
            .validFrom(permit.getValidFrom())
            .validUntil(permit.getValidUntil())
            .clearance(PermitResponse.ClearanceSummary.builder()
                .id(permit.getClearance().getId())
                .clearanceNumber(permit.getClearance().getClearanceNumber())
                .expiryDate(permit.getClearance().getExpiryDate())
                .build())
            .livestock(PermitResponse.LivestockSummary.builder()
                .id(permit.getLivestock().getId())
                .tagCode(permit.getLivestock().getTagCode())
                .name(permit.getLivestock().getName())
                .build())
            .issuedBy(PermitResponse.OfficerSummary.builder()
                .id(permit.getIssuedBy().getId())
                .officerCode(permit.getIssuedBy().getOfficerCode())
                .fullName(permit.getIssuedBy().getFullName())
                .role(permit.getIssuedBy().getRole().name())
                .build())
            .qrRef(permit.getQrRef())
            .pdfRef(permit.getPdfRef())
            .issueLatitude(permit.getIssueLatitude())
            .issueLongitude(permit.getIssueLongitude())
            .completedAt(permit.getCompletedAt())
            .completionLatitude(permit.getCompletionLatitude())
            .completionLongitude(permit.getCompletionLongitude())
            .verificationCount(verificationCount)
            .createdAt(permit.getCreatedAt())
            .updatedAt(permit.getUpdatedAt())
            .build();
    }
}
