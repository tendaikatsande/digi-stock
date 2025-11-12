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
import zw.co.digistock.dto.request.RegisterLivestockRequest;
import zw.co.digistock.dto.response.LivestockResponse;
import zw.co.digistock.service.LivestockService;
import zw.co.digistock.util.Constants;

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
     * Only AGRITEX officers and admins can register livestock
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<LivestockResponse> registerLivestock(
            @Valid @RequestBody RegisterLivestockRequest request) {
        log.info("POST /api/v1/livestock - Register new livestock: {}", request.getTagCode());
        LivestockResponse response = livestockService.registerLivestock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Upload photo for livestock
     * Only AGRITEX officers and admins can upload photos
     */
    @PostMapping("/{id}/photos")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
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
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<LivestockResponse> getLivestockById(@PathVariable UUID id) {
        log.info("GET /api/v1/livestock/{}", id);
        LivestockResponse response = livestockService.getLivestockById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get livestock by tag code
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/tag/{tagCode}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<LivestockResponse> getLivestockByTagCode(@PathVariable String tagCode) {
        log.info("GET /api/v1/livestock/tag/{}", tagCode);
        LivestockResponse response = livestockService.getLivestockByTagCode(tagCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get livestock by owner (paginated)
     * Accessible by AGRITEX officers, police officers, and admins
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<LivestockResponse>> getLivestockByOwner(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/livestock/owner/{} (page: {}, size: {})", ownerId, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LivestockResponse> response = livestockService.getLivestockByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get offspring by mother (paginated)
     * Accessible by AGRITEX officers and admins
     */
    @GetMapping("/{id}/offspring/mother")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<LivestockResponse>> getOffspringByMother(
            @PathVariable UUID id,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/livestock/{}/offspring/mother", id);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LivestockResponse> response = livestockService.getOffspringByMotherId(id, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get offspring by father (paginated)
     * Accessible by AGRITEX officers and admins
     */
    @GetMapping("/{id}/offspring/father")
    @PreAuthorize("hasAnyRole('AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<LivestockResponse>> getOffspringByFather(
            @PathVariable UUID id,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/livestock/{}/offspring/father", id);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LivestockResponse> response = livestockService.getOffspringByFatherId(id, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark livestock as stolen
     * Only police officers and admins can mark livestock as stolen
     */
    @PostMapping("/{id}/mark-stolen")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<LivestockResponse> markAsStolen(@PathVariable UUID id) {
        log.info("POST /api/v1/livestock/{}/mark-stolen", id);
        LivestockResponse response = livestockService.markAsStolen(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark livestock as recovered
     * Only police officers and admins can mark livestock as recovered
     */
    @PostMapping("/{id}/mark-recovered")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<LivestockResponse> markAsRecovered(@PathVariable UUID id) {
        log.info("POST /api/v1/livestock/{}/mark-recovered", id);
        LivestockResponse response = livestockService.markAsRecovered(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get stolen livestock (paginated)
     * Accessible by police officers and admins
     */
    @GetMapping("/stolen")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<LivestockResponse>> getStolenLivestock(
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/livestock/stolen (page: {}, size: {})", page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LivestockResponse> response = livestockService.getStolenLivestock(pageable);
        return ResponseEntity.ok(response);
    }
}
