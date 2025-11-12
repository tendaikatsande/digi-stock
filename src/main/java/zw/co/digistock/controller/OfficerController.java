package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.domain.enums.UserRole;
import zw.co.digistock.dto.request.UpdateOfficerRequest;
import zw.co.digistock.dto.response.OfficerResponse;
import zw.co.digistock.service.OfficerService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for officer management operations
 * Separate from AuthController which handles authentication
 */
@RestController
@RequestMapping("/api/v1/officers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Officer Management", description = "Endpoints for managing officer profiles and information")
public class OfficerController {

    private final OfficerService officerService;

    /**
     * Get all officers (paginated)
     * Only admins can view all officers
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all officers", description = "Retrieve paginated list of all officers in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved officers"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<Page<OfficerResponse>> getAllOfficers(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.info("GET /api/v1/officers - Get all officers");
        Page<OfficerResponse> response = officerService.getAllOfficers(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGRITEX_OFFICER', 'POLICE_OFFICER')")
    @Operation(summary = "Get officer by ID", description = "Retrieve officer details by their unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer found"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> getOfficerById(
            @Parameter(description = "Officer UUID") @PathVariable UUID id) {
        log.info("GET /api/v1/officers/{}", id);
        OfficerResponse response = officerService.getOfficerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officer by officer code
     */
    @GetMapping("/code/{officerCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGRITEX_OFFICER', 'POLICE_OFFICER')")
    @Operation(summary = "Get officer by code", description = "Retrieve officer details by their officer code")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer found"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> getOfficerByCode(
            @Parameter(description = "Officer code", example = "AGRI-HA-001") @PathVariable String officerCode) {
        log.info("GET /api/v1/officers/code/{}", officerCode);
        OfficerResponse response = officerService.getOfficerByCode(officerCode);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officer by email
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get officer by email", description = "Retrieve officer details by their email address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer found"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> getOfficerByEmail(
            @Parameter(description = "Email address") @PathVariable String email) {
        log.info("GET /api/v1/officers/email/{}", email);
        OfficerResponse response = officerService.getOfficerByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officers by role
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get officers by role", description = "Retrieve all officers with a specific role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officers retrieved successfully")
    })
    public ResponseEntity<List<OfficerResponse>> getOfficersByRole(
            @Parameter(description = "Officer role") @PathVariable UserRole role) {
        log.info("GET /api/v1/officers/role/{}", role);
        List<OfficerResponse> response = officerService.getOfficersByRole(role);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officers by district
     */
    @GetMapping("/district/{district}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGRITEX_OFFICER', 'POLICE_OFFICER')")
    @Operation(summary = "Get officers by district", description = "Retrieve all officers operating in a specific district")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officers retrieved successfully")
    })
    public ResponseEntity<List<OfficerResponse>> getOfficersByDistrict(
            @Parameter(description = "District code") @PathVariable String district) {
        log.info("GET /api/v1/officers/district/{}", district);
        List<OfficerResponse> response = officerService.getOfficersByDistrict(district);
        return ResponseEntity.ok(response);
    }

    /**
     * Get officers by province
     */
    @GetMapping("/province/{province}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGRITEX_OFFICER', 'POLICE_OFFICER')")
    @Operation(summary = "Get officers by province", description = "Retrieve all officers operating in a specific province")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officers retrieved successfully")
    })
    public ResponseEntity<List<OfficerResponse>> getOfficersByProvince(
            @Parameter(description = "Province name") @PathVariable String province) {
        log.info("GET /api/v1/officers/province/{}", province);
        List<OfficerResponse> response = officerService.getOfficersByProvince(province);
        return ResponseEntity.ok(response);
    }

    /**
     * Get active officers
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active officers", description = "Retrieve all officers with active accounts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officers retrieved successfully")
    })
    public ResponseEntity<List<OfficerResponse>> getActiveOfficers() {
        log.info("GET /api/v1/officers/active");
        List<OfficerResponse> response = officerService.getActiveOfficers();
        return ResponseEntity.ok(response);
    }

    /**
     * Update officer information
     * Officers can update their own profile, admins can update any profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Update officer information", description = "Update officer profile details. Role and officer code cannot be changed.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Officer not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<OfficerResponse> updateOfficer(
            @Parameter(description = "Officer UUID") @PathVariable UUID id,
            @Valid @RequestBody UpdateOfficerRequest request) {
        log.info("PUT /api/v1/officers/{} - Update officer", id);
        OfficerResponse response = officerService.updateOfficer(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload officer photo
     */
    @PostMapping("/{id}/photo")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Upload officer photo", description = "Upload profile photo for an officer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Photo uploaded successfully"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> uploadPhoto(
            @Parameter(description = "Officer UUID") @PathVariable UUID id,
            @Parameter(description = "Photo file") @RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/officers/{}/photo - Upload photo", id);
        OfficerResponse response = officerService.uploadPhoto(id, file);
        return ResponseEntity.ok(response);
    }

    /**
     * Enroll officer fingerprint
     */
    @PostMapping("/{id}/fingerprint")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Enroll officer fingerprint", description = "Enroll biometric fingerprint for an officer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fingerprint enrolled successfully"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> enrollFingerprint(
            @Parameter(description = "Officer UUID") @PathVariable UUID id,
            @Parameter(description = "Fingerprint image file") @RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/officers/{}/fingerprint - Enroll fingerprint", id);
        OfficerResponse response = officerService.enrollFingerprint(id, file);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate officer account
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate officer", description = "Deactivate an officer account (soft delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> deactivateOfficer(
            @Parameter(description = "Officer UUID") @PathVariable UUID id) {
        log.info("POST /api/v1/officers/{}/deactivate", id);
        OfficerResponse response = officerService.deactivateOfficer(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Reactivate officer account
     */
    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivate officer", description = "Reactivate a deactivated officer account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Officer reactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Officer not found")
    })
    public ResponseEntity<OfficerResponse> reactivateOfficer(
            @Parameter(description = "Officer UUID") @PathVariable UUID id) {
        log.info("POST /api/v1/officers/{}/reactivate", id);
        OfficerResponse response = officerService.reactivateOfficer(id);
        return ResponseEntity.ok(response);
    }
}
