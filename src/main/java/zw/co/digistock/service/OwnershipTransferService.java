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
import zw.co.digistock.domain.Officer;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.domain.OwnershipTransfer;
import zw.co.digistock.domain.enums.TransferStatus;
import zw.co.digistock.dto.request.InitiateTransferRequest;
import zw.co.digistock.dto.response.LivestockResponse;
import zw.co.digistock.dto.response.TransferResponse;
import zw.co.digistock.exception.BusinessException;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.LivestockRepository;
import zw.co.digistock.repository.OfficerRepository;
import zw.co.digistock.repository.OwnerRepository;
import zw.co.digistock.repository.OwnershipTransferRepository;
import zw.co.digistock.service.storage.MinioStorageService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnershipTransferService implements IOwnershipTransferService {

    private final OwnershipTransferRepository transferRepository;
    private final LivestockRepository livestockRepository;
    private final OwnerRepository ownerRepository;
    private final OfficerRepository officerRepository;
    private final MinioStorageService minioStorageService;
    private final MinioConfig minioConfig;

    @Override
    @Transactional
    public TransferResponse initiateTransfer(InitiateTransferRequest request, UUID officerId) {
        log.info("Initiating ownership transfer for livestock ID: {}", request.getLivestockId());

        Livestock livestock = livestockRepository.findById(request.getLivestockId())
                .orElseThrow(() -> new ResourceNotFoundException("Livestock", "id", request.getLivestockId()));

        if (transferRepository.existsByLivestockIdAndStatus(request.getLivestockId(), TransferStatus.PENDING)) {
            throw new BusinessException("Livestock already has a pending ownership transfer");
        }

        Owner fromOwner = livestock.getOwner();
        Owner toOwner = ownerRepository.findById(request.getToOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", request.getToOwnerId()));

        if (fromOwner.getId().equals(toOwner.getId())) {
            throw new BusinessException("Cannot transfer livestock to the same owner");
        }

        Officer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer", "id", officerId));

        OwnershipTransfer transfer = OwnershipTransfer.builder()
                .livestock(livestock)
                .fromOwner(fromOwner)
                .toOwner(toOwner)
                .initiatedBy(officer)
                .status(TransferStatus.PENDING)
                .transferDate(request.getTransferDate())
                .reason(request.getReason())
                .build();

        transfer = transferRepository.save(transfer);
        log.info("Ownership transfer initiated with ID: {}", transfer.getId());
        return toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse confirmByCurrentOwner(UUID transferId, MultipartFile fingerprintFile) {
        log.info("Current owner confirming transfer ID: {}", transferId);
        OwnershipTransfer transfer = getTransferOrThrow(transferId);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException("Transfer must be in PENDING status to confirm");
        }

        if (fingerprintFile != null && !fingerprintFile.isEmpty()) {
            String ref = minioStorageService.uploadFile(
                    fingerprintFile,
                    minioConfig.getFingerprintsBucket(),
                    "transfers/" + transferId + "/from-owner"
            );
            transfer.setFromOwnerFingerprintRef(ref);
        }

        transfer.setFromOwnerConfirmed(true);
        updateStatusIfBothConfirmed(transfer);
        transfer = transferRepository.save(transfer);
        return toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse confirmByNewOwner(UUID transferId, MultipartFile fingerprintFile) {
        log.info("New owner confirming transfer ID: {}", transferId);
        OwnershipTransfer transfer = getTransferOrThrow(transferId);

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException("Transfer must be in PENDING status to confirm");
        }

        if (fingerprintFile != null && !fingerprintFile.isEmpty()) {
            String ref = minioStorageService.uploadFile(
                    fingerprintFile,
                    minioConfig.getFingerprintsBucket(),
                    "transfers/" + transferId + "/to-owner"
            );
            transfer.setToOwnerFingerprintRef(ref);
        }

        transfer.setToOwnerConfirmed(true);
        updateStatusIfBothConfirmed(transfer);
        transfer = transferRepository.save(transfer);
        return toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse completeTransfer(UUID transferId, UUID officerId) {
        log.info("Completing transfer ID: {}", transferId);
        OwnershipTransfer transfer = getTransferOrThrow(transferId);

        if (transfer.getStatus() != TransferStatus.CONFIRMED) {
            throw new BusinessException("Transfer must be in CONFIRMED status to complete");
        }

        Livestock livestock = transfer.getLivestock();
        livestock.setOwner(transfer.getToOwner());
        livestockRepository.save(livestock);

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer = transferRepository.save(transfer);
        log.info("Ownership transfer completed. Livestock {} now belongs to owner {}",
                livestock.getTagCode(), transfer.getToOwner().getId());
        return toResponse(transfer);
    }

    @Override
    @Transactional
    public TransferResponse cancelTransfer(UUID transferId, UUID officerId, String reason) {
        log.info("Cancelling transfer ID: {}", transferId);
        OwnershipTransfer transfer = getTransferOrThrow(transferId);

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed transfer");
        }
        if (transfer.getStatus() == TransferStatus.CANCELLED) {
            throw new BusinessException("Transfer is already cancelled");
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        transfer.setCancellationReason(reason);
        transfer = transferRepository.save(transfer);
        return toResponse(transfer);
    }

    @Override
    public TransferResponse getById(UUID transferId) {
        return toResponse(getTransferOrThrow(transferId));
    }

    @Override
    public Page<TransferResponse> getByLivestock(UUID livestockId, Pageable pageable) {
        return transferRepository.findByLivestockId(livestockId, pageable).map(this::toResponse);
    }

    @Override
    public Page<TransferResponse> getByStatus(TransferStatus status, Pageable pageable) {
        return transferRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    // --- helpers ---

    private OwnershipTransfer getTransferOrThrow(UUID transferId) {
        return transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("OwnershipTransfer", "id", transferId));
    }

    private void updateStatusIfBothConfirmed(OwnershipTransfer transfer) {
        if (transfer.isFromOwnerConfirmed() && transfer.isToOwnerConfirmed()) {
            transfer.setStatus(TransferStatus.CONFIRMED);
        }
    }

    private TransferResponse toResponse(OwnershipTransfer t) {
        Livestock l = t.getLivestock();
        Owner from = t.getFromOwner();
        Owner to = t.getToOwner();

        return TransferResponse.builder()
                .id(t.getId())
                .livestock(LivestockResponse.LivestockSummary.builder()
                        .id(l.getId())
                        .tagCode(l.getTagCode())
                        .name(l.getName())
                        .breed(l.getBreed())
                        .build())
                .fromOwner(LivestockResponse.OwnerSummary.builder()
                        .id(from.getId())
                        .nationalId(from.getNationalId())
                        .fullName(from.getFullName())
                        .phoneNumber(from.getPhoneNumber())
                        .district(from.getDistrict())
                        .province(from.getProvince())
                        .build())
                .toOwner(LivestockResponse.OwnerSummary.builder()
                        .id(to.getId())
                        .nationalId(to.getNationalId())
                        .fullName(to.getFullName())
                        .phoneNumber(to.getPhoneNumber())
                        .district(to.getDistrict())
                        .province(to.getProvince())
                        .build())
                .initiatedBy(t.getInitiatedBy().getEmail())
                .status(t.getStatus())
                .transferDate(t.getTransferDate())
                .reason(t.getReason())
                .fromOwnerConfirmed(t.isFromOwnerConfirmed())
                .toOwnerConfirmed(t.isToOwnerConfirmed())
                .cancellationReason(t.getCancellationReason())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
