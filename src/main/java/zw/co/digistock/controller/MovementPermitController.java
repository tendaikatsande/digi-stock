package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.domain.enums.PermitStatus;
import zw.co.digistock.dto.request.CreatePermitRequest;
import zw.co.digistock.dto.response.PermitResponse;
import zw.co.digistock.service.MovementPermitService;
import zw.co.digistock.util.Constants;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for movement permit management
 */
@RestController
@RequestMapping("/api/v1/permits")
@RequiredArgsConstructor
@Slf4j
public class MovementPermitController {

    private final MovementPermitService permitService;

    /**
     * Create movement permit
     * Only AGRITEX officers and admins can create movement permits
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> createPermit(
            @Valid @RequestBody CreatePermitRequest request,
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/permits - Create permit for livestock: {}", request.getLivestockId());
        PermitResponse response = permitService.createPermit(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verify permit at checkpoint
     * Only police officers and admins can verify permits at checkpoints
     */
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> verifyPermit(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Officer-Id") UUID officerId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String notes) {
        log.info("POST /api/v1/permits/{}/verify", id);
        PermitResponse response = permitService.verifyPermit(id, officerId, latitude, longitude, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete movement
     * Only AGRITEX officers and admins can complete permits
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> completePermit(
            @PathVariable UUID id,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        log.info("POST /api/v1/permits/{}/complete", id);
        PermitResponse response = permitService.completePermit(id, latitude, longitude);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel permit
     * Only AGRITEX officers and admins can cancel permits
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> cancelPermit(
            @PathVariable UUID id,
            @RequestParam("reason") String reason) {
        log.info("POST /api/v1/permits/{}/cancel", id);
        PermitResponse response = permitService.cancelPermit(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permit by ID
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> getPermitById(@PathVariable UUID id) {
        log.info("GET /api/v1/permits/{}", id);
        PermitResponse response = permitService.getPermitById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permit by permit number
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/number/{permitNumber}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<PermitResponse> getPermitByNumber(@PathVariable String permitNumber) {
        log.info("GET /api/v1/permits/number/{}", permitNumber);
        PermitResponse response = permitService.getPermitByNumber(permitNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permits for livestock (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/livestock/{livestockId}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<PermitResponse>> getPermitsByLivestock(
            @PathVariable UUID livestockId,
            Pageable pageable) {
        log.info("GET /api/v1/permits/livestock/{}", livestockId);
        Page<PermitResponse> response = permitService.getPermitsByLivestock(livestockId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permits by status (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<PermitResponse>> getPermitsByStatus(
            @PathVariable PermitStatus status,
            Pageable pageable) {
        log.info("GET /api/v1/permits/status/{}", status);
        Page<PermitResponse> response = permitService.getPermitsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get valid permits (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/valid")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<PermitResponse>> getValidPermits(Pageable pageable) {
        log.info("GET /api/v1/permits/valid");
        Page<PermitResponse> response = permitService.getValidPermits(pageable);
        return ResponseEntity.ok(response);
    }
}
