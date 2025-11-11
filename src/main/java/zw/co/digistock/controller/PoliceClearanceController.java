package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.dto.request.CreateClearanceRequest;
import zw.co.digistock.dto.response.ClearanceResponse;
import zw.co.digistock.service.PoliceClearanceService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for police clearance management
 */
@RestController
@RequestMapping("/api/v1/clearances")
@RequiredArgsConstructor
@Slf4j
public class PoliceClearanceController {

    private final PoliceClearanceService clearanceService;

    /**
     * Create police clearance
     * TODO: Get officer ID from authentication context
     */
    @PostMapping
    public ResponseEntity<ClearanceResponse> createClearance(
            @Valid @RequestBody CreateClearanceRequest request,
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/clearances - Create clearance for livestock: {}", request.getLivestockId());
        ClearanceResponse response = clearanceService.createClearance(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Approve clearance
     * TODO: Get officer ID from authentication context
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ClearanceResponse> approveClearance(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/clearances/{}/approve", id);
        ClearanceResponse response = clearanceService.approveClearance(id, officerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject clearance
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ClearanceResponse> rejectClearance(
            @PathVariable UUID id,
            @RequestParam("reason") String reason) {
        log.info("POST /api/v1/clearances/{}/reject", id);
        ClearanceResponse response = clearanceService.rejectClearance(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * Get clearance by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClearanceResponse> getClearanceById(@PathVariable UUID id) {
        log.info("GET /api/v1/clearances/{}", id);
        ClearanceResponse response = clearanceService.getClearanceById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get clearance by clearance number
     */
    @GetMapping("/number/{clearanceNumber}")
    public ResponseEntity<ClearanceResponse> getClearanceByClearanceNumber(
            @PathVariable String clearanceNumber) {
        log.info("GET /api/v1/clearances/number/{}", clearanceNumber);
        ClearanceResponse response = clearanceService.getClearanceByClearanceNumber(clearanceNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get valid clearances for livestock
     */
    @GetMapping("/livestock/{livestockId}/valid")
    public ResponseEntity<List<ClearanceResponse>> getValidClearancesForLivestock(
            @PathVariable UUID livestockId) {
        log.info("GET /api/v1/clearances/livestock/{}/valid", livestockId);
        List<ClearanceResponse> response = clearanceService.getValidClearancesForLivestock(livestockId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get clearances by owner
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ClearanceResponse>> getClearancesByOwner(@PathVariable UUID ownerId) {
        log.info("GET /api/v1/clearances/owner/{}", ownerId);
        List<ClearanceResponse> response = clearanceService.getClearancesByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get pending clearances
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ClearanceResponse>> getPendingClearances() {
        log.info("GET /api/v1/clearances/pending");
        List<ClearanceResponse> response = clearanceService.getPendingClearances();
        return ResponseEntity.ok(response);
    }
}
