package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Get permits for livestock (paginated)
     */
    @GetMapping("/livestock/{livestockId}")
    public ResponseEntity<Page<PermitResponse>> getPermitsByLivestock(
            @PathVariable UUID livestockId,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = "issuedAt") String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/permits/livestock/{} (page: {}, size: {})", livestockId, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermitResponse> response = permitService.getPermitsByLivestock(livestockId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get permits by status (paginated)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PermitResponse>> getPermitsByStatus(
            @PathVariable PermitStatus status,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = "issuedAt") String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/permits/status/{} (page: {}, size: {})", status, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermitResponse> response = permitService.getPermitsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get valid permits (paginated)
     */
    @GetMapping("/valid")
    public ResponseEntity<Page<PermitResponse>> getValidPermits(
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = "issuedAt") String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/permits/valid (page: {}, size: {})", page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermitResponse> response = permitService.getValidPermits(pageable);
        return ResponseEntity.ok(response);
    }
}
