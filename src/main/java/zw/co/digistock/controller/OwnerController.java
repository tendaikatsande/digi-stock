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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.dto.request.RegisterOwnerRequest;
import zw.co.digistock.dto.response.OwnerResponse;
import zw.co.digistock.service.OwnerService;
import zw.co.digistock.util.Constants;

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
     * Only AGRITEX officers and admins can register owners
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> registerOwner(
            @Valid @RequestBody RegisterOwnerRequest request) {
        log.info("POST /api/v1/owners - Register new owner");
        OwnerResponse response = ownerService.registerOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Enroll owner fingerprint
     * Only AGRITEX officers and admins can enroll fingerprints
     */
    @PostMapping("/{id}/fingerprint")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> enrollFingerprint(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile fingerprintImage) {
        log.info("POST /api/v1/owners/{}/fingerprint - Enroll fingerprint", id);
        OwnerResponse response = ownerService.enrollFingerprint(id, fingerprintImage);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload owner photo
     * Only AGRITEX officers and admins can upload photos
     */
    @PostMapping("/{id}/photo")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile photo) {
        log.info("POST /api/v1/owners/{}/photo - Upload photo", id);
        OwnerResponse response = ownerService.uploadPhoto(id, photo);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owner by ID
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable UUID id) {
        log.info("GET /api/v1/owners/{}", id);
        OwnerResponse response = ownerService.getOwnerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owner by national ID
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/national-id/{nationalId}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> getOwnerByNationalId(@PathVariable String nationalId) {
        log.info("GET /api/v1/owners/national-id/{}", nationalId);
        OwnerResponse response = ownerService.getOwnerByNationalId(nationalId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get owners by district (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/district/{district}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<OwnerResponse>> getOwnersByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/owners/district/{} (page: {}, size: {})", district, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OwnerResponse> response = ownerService.getOwnersByDistrict(district, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Search owners by name (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<OwnerResponse>> searchOwners(
            @RequestParam("q") String searchTerm,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/owners/search?q={} (page: {}, size: {})", searchTerm, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OwnerResponse> response = ownerService.searchOwnersByName(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all owners (paginated)
     * Accessible by AGRITEX officers and admins
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<OwnerResponse>> getAllOwners(
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/owners (page: {}, size: {})", page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OwnerResponse> response = ownerService.getAllOwners(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update owner
     * Only AGRITEX officers and admins can update owners
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<OwnerResponse> updateOwner(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterOwnerRequest request) {
        log.info("PUT /api/v1/owners/{}", id);
        OwnerResponse response = ownerService.updateOwner(id, request);
        return ResponseEntity.ok(response);
    }
}
