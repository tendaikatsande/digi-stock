package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.domain.enums.TransferStatus;
import zw.co.digistock.dto.request.InitiateTransferRequest;
import zw.co.digistock.dto.response.TransferResponse;
import zw.co.digistock.service.IOwnershipTransferService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ownership Transfers", description = "Livestock ownership transfer workflow")
public class OwnershipTransferController {

    private final IOwnershipTransferService transferService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Initiate ownership transfer", description = "Start a new ownership transfer for a livestock animal")
    public ResponseEntity<TransferResponse> initiateTransfer(
            @Valid @RequestBody InitiateTransferRequest request,
            @RequestHeader("X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/transfers - Initiate transfer for livestock: {}", request.getLivestockId());
        TransferResponse response = transferService.initiateTransfer(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/confirm-current-owner")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Confirm transfer by current owner", description = "Current owner confirms the transfer, optionally providing a fingerprint")
    public ResponseEntity<TransferResponse> confirmByCurrentOwner(
            @PathVariable UUID id,
            @RequestParam(value = "fingerprint", required = false) MultipartFile fingerprintFile) {
        log.info("POST /api/v1/transfers/{}/confirm-current-owner", id);
        TransferResponse response = transferService.confirmByCurrentOwner(id, fingerprintFile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm-new-owner")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Confirm transfer by new owner", description = "New owner confirms the transfer, optionally providing a fingerprint")
    public ResponseEntity<TransferResponse> confirmByNewOwner(
            @PathVariable UUID id,
            @RequestParam(value = "fingerprint", required = false) MultipartFile fingerprintFile) {
        log.info("POST /api/v1/transfers/{}/confirm-new-owner", id);
        TransferResponse response = transferService.confirmByNewOwner(id, fingerprintFile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Complete ownership transfer", description = "Finalise the transfer — livestock owner is updated in the database")
    public ResponseEntity<TransferResponse> completeTransfer(
            @PathVariable UUID id,
            @RequestHeader("X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/transfers/{}/complete", id);
        TransferResponse response = transferService.completeTransfer(id, officerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Cancel ownership transfer", description = "Cancel a pending or confirmed transfer")
    public ResponseEntity<TransferResponse> cancelTransfer(
            @PathVariable UUID id,
            @RequestHeader("X-Officer-Id") UUID officerId,
            @RequestParam("reason") String reason) {
        log.info("POST /api/v1/transfers/{}/cancel", id);
        TransferResponse response = transferService.cancelTransfer(id, officerId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get transfer by ID")
    public ResponseEntity<TransferResponse> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/transfers/{}", id);
        return ResponseEntity.ok(transferService.getById(id));
    }

    @GetMapping("/livestock/{livestockId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get transfers for a livestock animal")
    public ResponseEntity<Page<TransferResponse>> getByLivestock(
            @PathVariable UUID livestockId,
            Pageable pageable) {
        log.info("GET /api/v1/transfers/livestock/{}", livestockId);
        return ResponseEntity.ok(transferService.getByLivestock(livestockId, pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Get transfers by status")
    public ResponseEntity<Page<TransferResponse>> getByStatus(
            @PathVariable TransferStatus status,
            Pageable pageable) {
        log.info("GET /api/v1/transfers/status/{}", status);
        return ResponseEntity.ok(transferService.getByStatus(status, pageable));
    }
}
