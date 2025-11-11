package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.dto.request.RegisterLivestockRequest;
import zw.co.digistock.dto.response.LivestockResponse;
import zw.co.digistock.service.LivestockService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for livestock management
 */
@RestController
@RequestMapping("/api/v1/livestock")
@RequiredArgsConstructor
@Slf4j
public class LivestockController {

    private final LivestockService livestockService;

    /**
     * Register new livestock
     */
    @PostMapping
    public ResponseEntity<LivestockResponse> registerLivestock(
            @Valid @RequestBody RegisterLivestockRequest request) {
        log.info("POST /api/v1/livestock - Register new livestock: {}", request.getTagCode());
        LivestockResponse response = livestockService.registerLivestock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload photo for livestock
     */
    @PostMapping("/{id}/photos")
    public ResponseEntity<LivestockResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "photoType", required = false) String photoType) {
        log.info("POST /api/v1/livestock/{}/photos - Upload photo", id);
        LivestockResponse response = livestockService.uploadPhoto(id, file, description, photoType);
        return ResponseEntity.ok(response);
    }

    /**
     * Get livestock by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LivestockResponse> getLivestockById(@PathVariable UUID id) {
        log.info("GET /api/v1/livestock/{}", id);
        LivestockResponse response = livestockService.getLivestockById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get livestock by tag code
     */
    @GetMapping("/tag/{tagCode}")
    public ResponseEntity<LivestockResponse> getLivestockByTagCode(@PathVariable String tagCode) {
        log.info("GET /api/v1/livestock/tag/{}", tagCode);
        LivestockResponse response = livestockService.getLivestockByTagCode(tagCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get livestock by owner
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<LivestockResponse>> getLivestockByOwner(@PathVariable UUID ownerId) {
        log.info("GET /api/v1/livestock/owner/{}", ownerId);
        List<LivestockResponse> response = livestockService.getLivestockByOwner(ownerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get offspring of livestock
     */
    @GetMapping("/{id}/offspring")
    public ResponseEntity<List<LivestockResponse>> getOffspring(@PathVariable UUID id) {
        log.info("GET /api/v1/livestock/{}/offspring", id);
        List<LivestockResponse> response = livestockService.getOffspring(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark livestock as stolen
     */
    @PostMapping("/{id}/mark-stolen")
    public ResponseEntity<LivestockResponse> markAsStolen(@PathVariable UUID id) {
        log.info("POST /api/v1/livestock/{}/mark-stolen", id);
        LivestockResponse response = livestockService.markAsStolen(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark livestock as recovered
     */
    @PostMapping("/{id}/mark-recovered")
    public ResponseEntity<LivestockResponse> markAsRecovered(@PathVariable UUID id) {
        log.info("POST /api/v1/livestock/{}/mark-recovered", id);
        LivestockResponse response = livestockService.markAsRecovered(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get stolen livestock
     */
    @GetMapping("/stolen")
    public ResponseEntity<List<LivestockResponse>> getStolenLivestock() {
        log.info("GET /api/v1/livestock/stolen");
        List<LivestockResponse> response = livestockService.getStolenLivestock();
        return ResponseEntity.ok(response);
    }
}
