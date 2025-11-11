package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.PoliceClearance;
import zw.co.digistock.domain.enums.ClearanceStatus;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.dto.request.CreateClearanceRequest;
import zw.co.digistock.dto.response.ClearanceResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.repository.PoliceClearanceRepository;
import zw.co.digistock.service.qr.QrCodeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for police clearance operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceClearanceService {

    private final PoliceClearanceRepository clearanceRepository;
    private final LivestockRepository livestockRepository;
    private final OwnerRepository ownerRepository;
    private final OfficerRepository officerRepository;
    private final QrCodeService qrCodeService;

    @Value("${digistock.clearance.default-validity-days:14}")
    private int defaultValidityDays;

    /**
     * Create new police clearance
     */
    @Transactional
    public ClearanceResponse createClearance(CreateClearanceRequest request, UUID officerId) {
        log.info("Creating police clearance for livestock: {}", request.getLivestockId());

        // Verify officer is police
        Officer officer = officerRepository.findById(officerId)
            .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        if (officer.getRole() != UserRole.POLICE_OFFICER && officer.getRole() != UserRole.ADMIN) {
            throw new BusinessException("Only police officers can issue clearances");
        }

        // Verify livestock exists and is not stolen
        Livestock livestock = livestockRepository.findById(request.getLivestockId())
            .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", request.getLivestockId()));

        if (livestock.isStolen()) {
            throw new BusinessException("Cannot issue clearance for stolen livestock: " + livestock.getTagCode());
        }

        // Verify owner
        Owner owner = ownerRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", request.getOwnerId()));

        // Verify ownership matches
        if (!livestock.getOwner().getId().equals(owner.getId())) {
            throw new BusinessException("Livestock ownership mismatch. Owner ID does not match livestock owner.");
        }

        // Generate clearance number
        String clearanceNumber = generateClearanceNumber(officer.getProvince());

        // Calculate expiry date
        LocalDate expiryDate = LocalDate.now().plusDays(defaultValidityDays);

        // Create clearance
        PoliceClearance clearance = PoliceClearance.builder()
            .clearanceNumber(clearanceNumber)
            .livestock(livestock)
            .owner(owner)
            .issuedBy(officer)
            .status(ClearanceStatus.PENDING)
            .clearanceDate(LocalDateTime.now())
            .expiryDate(expiryDate)
            .issueLatitude(request.getIssueLatitude())
            .issueLongitude(request.getIssueLongitude())
            .build();

        PoliceClearance saved = clearanceRepository.save(clearance);
        log.info("Police clearance created: {}", clearanceNumber);

        return mapToResponse(saved);
    }

    /**
     * Approve clearance
     */
    @Transactional
    public ClearanceResponse approveClearance(UUID clearanceId, UUID officerId) {
        log.info("Approving clearance: {}", clearanceId);

        PoliceClearance clearance = clearanceRepository.findById(clearanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Police Clearance", "id", clearanceId));

        if (clearance.getStatus() != ClearanceStatus.PENDING) {
            throw new BusinessException("Clearance is not in PENDING status");
        }

        clearance.setStatus(ClearanceStatus.APPROVED);
        clearance.setClearanceDate(LocalDateTime.now());

        // Generate QR code
        String qrRef = qrCodeService.generateClearanceQrCode(
            clearance.getClearanceNumber(),
            clearance.getLivestock().getTagCode(),
            clearance.getExpiryDate().toString(),
            clearance.getId().toString()
        );
        clearance.setQrRef(qrRef);

        PoliceClearance updated = clearanceRepository.save(clearance);
        log.info("Clearance approved: {}", clearance.getClearanceNumber());

        return mapToResponse(updated);
    }

    /**
     * Reject clearance
     */
    @Transactional
    public ClearanceResponse rejectClearance(UUID clearanceId, String reason) {
        log.info("Rejecting clearance: {}", clearanceId);

        PoliceClearance clearance = clearanceRepository.findById(clearanceId)
            .orElseThrow(() -> new ResourceNotFoundException("Police Clearance", "id", clearanceId));

        if (clearance.getStatus() != ClearanceStatus.PENDING) {
            throw new BusinessException("Clearance is not in PENDING status");
        }

        clearance.setStatus(ClearanceStatus.REJECTED);
        clearance.setRejectionReason(reason);

        PoliceClearance updated = clearanceRepository.save(clearance);
        log.info("Clearance rejected: {}", clearance.getClearanceNumber());

        return mapToResponse(updated);
    }

    /**
     * Get clearance by ID
     */
    @Transactional(readOnly = true)
    public ClearanceResponse getClearanceById(UUID id) {
        PoliceClearance clearance = clearanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Police Clearance", "id", id));
        return mapToResponse(clearance);
    }

    /**
     * Get clearance by clearance number
     */
    @Transactional(readOnly = true)
    public ClearanceResponse getClearanceByClearanceNumber(String clearanceNumber) {
        PoliceClearance clearance = clearanceRepository.findByClearanceNumber(clearanceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Police Clearance", "clearanceNumber", clearanceNumber));
        return mapToResponse(clearance);
    }

    /**
     * Get valid clearances for livestock (paginated)
     */
    @Transactional(readOnly = true)
    public Page<ClearanceResponse> getValidClearancesForLivestock(UUID livestockId, Pageable pageable) {
        Page<PoliceClearance> page = clearanceRepository.findValidClearancesForLivestock(livestockId, LocalDate.now(), pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get clearances for owner (paginated)
     */
    @Transactional(readOnly = true)
    public Page<ClearanceResponse> getClearancesByOwner(UUID ownerId, Pageable pageable) {
        Page<PoliceClearance> page = clearanceRepository.findByOwnerId(ownerId, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Get pending clearances (paginated)
     */
    @Transactional(readOnly = true)
    public Page<ClearanceResponse> getPendingClearances(Pageable pageable) {
        Page<PoliceClearance> page = clearanceRepository.findByStatusOrderByClearanceDateDesc(ClearanceStatus.PENDING, pageable);
        return page.map(this::mapToResponse);
    }

    /**
     * Generate clearance number
     * Format: PC-{PROVINCE_CODE}-{SEQUENTIAL}
     */
    private String generateClearanceNumber(String province) {
        String provinceCode = province != null && province.length() >= 2
            ? province.substring(0, 2).toUpperCase()
            : "XX";

        long count = clearanceRepository.count() + 1;
        return String.format("PC-%s-%06d", provinceCode, count);
    }

    /**
     * Map entity to response DTO
     */
    private ClearanceResponse mapToResponse(PoliceClearance clearance) {
        return ClearanceResponse.builder()
            .id(clearance.getId())
            .clearanceNumber(clearance.getClearanceNumber())
            .status(clearance.getStatus())
            .clearanceDate(clearance.getClearanceDate())
            .expiryDate(clearance.getExpiryDate())
            .valid(clearance.isValid())
            .livestock(ClearanceResponse.LivestockSummary.builder()
                .id(clearance.getLivestock().getId())
                .tagCode(clearance.getLivestock().getTagCode())
                .name(clearance.getLivestock().getName())
                .build())
            .owner(ClearanceResponse.OwnerSummary.builder()
                .id(clearance.getOwner().getId())
                .nationalId(clearance.getOwner().getNationalId())
                .fullName(clearance.getOwner().getFullName())
                .phoneNumber(clearance.getOwner().getPhoneNumber())
                .build())
            .issuedBy(ClearanceResponse.OfficerSummary.builder()
                .id(clearance.getIssuedBy().getId())
                .officerCode(clearance.getIssuedBy().getOfficerCode())
                .fullName(clearance.getIssuedBy().getFullName())
                .role(clearance.getIssuedBy().getRole().name())
                .build())
            .rejectionReason(clearance.getRejectionReason())
            .qrRef(clearance.getQrRef())
            .pdfRef(clearance.getPdfRef())
            .issueLatitude(clearance.getIssueLatitude())
            .issueLongitude(clearance.getIssueLongitude())
            .createdAt(clearance.getCreatedAt())
            .updatedAt(clearance.getUpdatedAt())
            .build();
    }
}
