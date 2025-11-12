package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import zw.co.digistock.dto.request.CreateClearanceRequest;
import zw.co.digistock.dto.response.ClearanceResponse;
import zw.co.digistock.service.PoliceClearanceService;
import zw.co.digistock.util.Constants;

import java.util.UUID;

/**
 * REST controller for police clearance management
 */
@RestController
@RequestMapping("/api/v1/clearances")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Police Clearances", description = "Police clearance management endpoints for livestock movement authorization")
public class PoliceClearanceController {

    private final PoliceClearanceService clearanceService;

    @Operation(
        summary = "Create police clearance",
        description = "Creates a new police clearance for livestock. Only police officers can issue clearances."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Clearance created successfully",
            content = @Content(schema = @Schema(implementation = ClearanceResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Livestock or owner not found"),
        @ApiResponse(responseCode = "403", description = "Not authorized - only police officers can issue clearances")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<ClearanceResponse> createClearance(
            @Valid @RequestBody CreateClearanceRequest request,
            @Parameter(description = "Officer ID (temporary - will be replaced with JWT authentication)")
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/clearances - Create clearance for livestock: {}", request.getLivestockId());
        ClearanceResponse response = clearanceService.createClearance(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Approve clearance",
        description = "Approves a pending clearance and generates QR code for verification"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clearance approved successfully",
            content = @Content(schema = @Schema(implementation = ClearanceResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Clearance not found"),
        @ApiResponse(responseCode = "400", description = "Clearance is not in PENDING status")
    })
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<ClearanceResponse> approveClearance(
            @Parameter(description = "Clearance ID") @PathVariable UUID id,
            @Parameter(description = "Officer ID (temporary - will be replaced with JWT authentication)")
            @RequestHeader(value = "X-Officer-Id") UUID officerId) {
        log.info("POST /api/v1/clearances/{}/approve", id);
        ClearanceResponse response = clearanceService.approveClearance(id, officerId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Reject clearance",
        description = "Rejects a pending clearance with a reason"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clearance rejected successfully",
            content = @Content(schema = @Schema(implementation = ClearanceResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Clearance not found"),
        @ApiResponse(responseCode = "400", description = "Clearance is not in PENDING status")
    })
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<ClearanceResponse> rejectClearance(
            @Parameter(description = "Clearance ID") @PathVariable UUID id,
            @Parameter(description = "Reason for rejection", required = true) @RequestParam("reason") String reason) {
        log.info("POST /api/v1/clearances/{}/reject", id);
        ClearanceResponse response = clearanceService.rejectClearance(id, reason);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get clearance by ID",
        description = "Retrieves a police clearance by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clearance found",
            content = @Content(schema = @Schema(implementation = ClearanceResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Clearance not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<ClearanceResponse> getClearanceById(
            @Parameter(description = "Clearance UUID") @PathVariable UUID id) {
        log.info("GET /api/v1/clearances/{}", id);
        ClearanceResponse response = clearanceService.getClearanceById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get clearance by clearance number",
        description = "Retrieves a police clearance by its clearance number (e.g., PC-HR-000001)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clearance found",
            content = @Content(schema = @Schema(implementation = ClearanceResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Clearance not found")
    })
    @GetMapping("/number/{clearanceNumber}")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<ClearanceResponse> getClearanceByClearanceNumber(
            @Parameter(description = "Clearance number (e.g., PC-HR-000001)", example = "PC-HR-000001")
            @PathVariable String clearanceNumber) {
        log.info("GET /api/v1/clearances/number/{}", clearanceNumber);
        ClearanceResponse response = clearanceService.getClearanceByClearanceNumber(clearanceNumber);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get valid clearances for livestock",
        description = "Retrieves all valid (non-expired) clearances for a specific livestock animal with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Valid clearances retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Livestock not found")
    })
    @GetMapping("/livestock/{livestockId}/valid")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<ClearanceResponse>> getValidClearancesForLivestock(
            @Parameter(description = "Livestock UUID") @PathVariable UUID livestockId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @Parameter(description = "Page size (max 100)", example = "20") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @Parameter(description = "Sort field", example = "clearanceDate") @RequestParam(defaultValue = "clearanceDate") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/clearances/livestock/{}/valid (page: {}, size: {})", livestockId, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClearanceResponse> response = clearanceService.getValidClearancesForLivestock(livestockId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get clearances by owner",
        description = "Retrieves all clearances issued for a specific owner's livestock with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Owner clearances retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "404", description = "Owner not found")
    })
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'AGRITEX_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<ClearanceResponse>> getClearancesByOwner(
            @Parameter(description = "Owner UUID") @PathVariable UUID ownerId,
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @Parameter(description = "Page size (max 100)", example = "20") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @Parameter(description = "Sort field", example = "clearanceDate") @RequestParam(defaultValue = "clearanceDate") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/clearances/owner/{} (page: {}, size: {})", ownerId, page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClearanceResponse> response = clearanceService.getClearancesByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get pending clearances",
        description = "Retrieves all clearances with PENDING status awaiting approval with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pending clearances retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('POLICE_OFFICER', 'ADMIN')")
    public ResponseEntity<Page<ClearanceResponse>> getPendingClearances(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @Parameter(description = "Page size (max 100)", example = "20") @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE_STR) int size,
            @Parameter(description = "Sort field", example = "clearanceDate") @RequestParam(defaultValue = "clearanceDate") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String sortDir) {
        log.info("GET /api/v1/clearances/pending (page: {}, size: {})", page, size);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClearanceResponse> response = clearanceService.getPendingClearances(pageable);
        return ResponseEntity.ok(response);
    }
}
