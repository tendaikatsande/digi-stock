package zw.co.digistock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.dto.request.RegisterOwnerRequest;
import zw.co.digistock.dto.response.OwnerResponse;
import zw.co.digistock.service.OwnerService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for owner management
 */
@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
@Slf4j
public class OwnerController {

    private final OwnerService ownerService;

    /**
     * Register new owner
     */
    @PostMapping
    public ResponseEntity<OwnerResponse> registerOwner(
            @Valid @RequestBody RegisterOwnerRequest request) {
        log.info("POST /api/v1/owners - Register new owner");
        OwnerResponse response = ownerService.registerOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Enroll owner fingerprint
     */
    @PostMapping("/{id}/fingerprint")
    public ResponseEntity<OwnerResponse> enrollFingerprint(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile fingerprintImage) {
        log.info("POST /api/v1/owners/{}/fingerprint - Enroll fingerprint", id);
        OwnerResponse response = ownerService.enrollFingerprint(id, fingerprintImage);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload owner photo
     */
    @PostMapping("/{id}/photo")
    public ResponseEntity<OwnerResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile photo) {
        log.info("POST /api/v1/owners/{}/photo - Upload photo", id);
        OwnerResponse response = ownerService.uploadPhoto(id, photo);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owner by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable UUID id) {
        log.info("GET /api/v1/owners/{}", id);
        OwnerResponse response = ownerService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owner by national ID
     */
    @GetMapping("/national-id/{nationalId}")
    public ResponseEntity<OwnerResponse> getOwnerByNationalId(@PathVariable String nationalId) {
        log.info("GET /api/v1/owners/national-id/{}", nationalId);
        OwnerResponse response = ownerService.getOwnerByNationalId(nationalId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owners by district
     */
    @GetMapping("/district/{district}")
    public ResponseEntity<List<OwnerResponse>> getOwnersByDistrict(@PathVariable String district) {
        log.info("GET /api/v1/owners/district/{}", district);
        List<OwnerResponse> response = ownerService.getOwnersByDistrict(district);
        return ResponseEntity.ok(response);
    }

    /**
     * Search owners by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<OwnerResponse>> searchOwners(
            @RequestParam("q") String searchTerm) {
        log.info("GET /api/v1/owners/search?q={}", searchTerm);
        List<OwnerResponse> response = ownerService.searchOwnersByName(searchTerm);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all owners
     */
    @GetMapping
    public ResponseEntity<List<OwnerResponse>> getAllOwners() {
        log.info("GET /api/v1/owners");
        List<OwnerResponse> response = ownerService.getAllOwners();
        return ResponseEntity.ok(response);
    }

    /**
     * Update owner
     */
    @PutMapping("/{id}")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterOwnerRequest request) {
        log.info("PUT /api/v1/owners/{}", id);
        OwnerResponse response = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(response);
    }
}
