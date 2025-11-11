package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.domain.enums.PermitStatus;
import zw.co.digistock.dto.request.CreatePermitRequest;
import zw.co.digistock.dto.response.PermitResponse;
import zw.co.digistock.service.MovementPermitService;

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
     * TODO: Get officer ID from authentication context
     */
    @PostMapping
    public ResponseEntity<PermitResponse> createPermit(
            @Valid @RequestBody CreatePermitRequest request,
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/permits - Create permit for livestock: {}", request.getLivestockId());
        PermitResponse response = permitService.createPermit(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Verify permit at checkpoint
     * TODO: Get officer ID from authentication context
     */
    @PostMapping("/{id}/verify")
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
     */
    @PostMapping("/{id}/complete")
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
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PermitResponse> cancelPermit(
            @PathVariable UUID id,
            @RequestParam("reason") String reason) {
        log.info("POST /api/v1/permits/{}/cancel", id);
        PermitResponse response = permitService.cancelPermit(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermitResponse> getPermitById(@PathVariable UUID id) {
        log.info("GET /api/v1/permits/{}", id);
        PermitResponse response = permitService.getPermitById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permit by permit number
     */
    @GetMapping("/number/{permitNumber}")
    public ResponseEntity<PermitResponse> getPermitByNumber(@PathVariable String permitNumber) {
        log.info("GET /api/v1/permits/number/{}", permitNumber);
        PermitResponse response = permitService.getPermitByNumber(permitNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permits for livestock
     */
    @GetMapping("/livestock/{livestockId}")
    public ResponseEntity<List<PermitResponse>> getPermitsByLivestock(@PathVariable UUID livestockId) {
        log.info("GET /api/v1/permits/livestock/{}", livestockId);
        List<PermitResponse> response = permitService.getPermitsByLivestock(livestockId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permits by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PermitResponse>> getPermitsByStatus(@PathVariable PermitStatus status) {
        log.info("GET /api/v1/permits/status/{}", status);
        List<PermitResponse> response = permitService.getPermitsByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get valid permits
     */
    @GetMapping("/valid")
    public ResponseEntity<List<PermitResponse>> getValidPermits() {
        log.info("GET /api/v1/permits/valid");
        List<PermitResponse> response = permitService.getValidPermits();
        return ResponseEntity.ok(response);
    }
}
