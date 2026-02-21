package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.dto.request.CreateVaccinationRequest;
import zw.co.digistock.dto.request.UpdateVaccinationRequest;
import zw.co.digistock.dto.response.VaccinationResponse;
import zw.co.digistock.service.VaccinationService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing vaccination records.
 * Provides endpoints for creating, retrieving, updating, and deleting vaccination records.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/vaccinations")
@RequiredArgsConstructor
@Tag(
    name = "Vaccinations",
    description = "Operations related to livestock vaccination records"
)
public class VaccinationController {

    private final VaccinationService vaccinationService;

    /**
     * Create a new vaccination record.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','VETERINARY_OFFICER','AGRITEX_OFFICER')")
    @Operation(
        summary = "Create a new vaccination record",
        description = "Create a new vaccination record for a livestock animal"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination record created successfully",
            content = @Content(schema = @Schema(implementation = VaccinationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Livestock or veterinary officer not found",
            content = @Content
        )
    })
    public ResponseEntity<VaccinationResponse> createVaccination(
            @Valid @RequestBody CreateVaccinationRequest request) {
        log.info("POST /api/v1/vaccinations - Create vaccination request");
        VaccinationResponse response = vaccinationService.createVaccination(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a vaccination record by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccination record by ID",
        description = "Retrieve a vaccination record using its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination record retrieved successfully",
            content = @Content(schema = @Schema(implementation = VaccinationResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vaccination record not found",
            content = @Content
        )
    })
    public ResponseEntity<VaccinationResponse> getVaccinationById(@PathVariable UUID id) {
        log.info("GET /api/v1/vaccinations/{} - Get vaccination by ID", id);
        VaccinationResponse response = vaccinationService.getVaccinationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing vaccination record.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','VETERINARY_OFFICER')")
    @Operation(
        summary = "Update vaccination record",
        description = "Update an existing vaccination record"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination record updated successfully",
            content = @Content(schema = @Schema(implementation = VaccinationResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vaccination record not found",
            content = @Content
        )
    })
    public ResponseEntity<VaccinationResponse> updateVaccination(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVaccinationRequest request) {
        log.info("PUT /api/v1/vaccinations/{} - Update vaccination", id);
        VaccinationResponse response = vaccinationService.updateVaccination(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a vaccination record.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN')")
    @Operation(
        summary = "Delete vaccination record",
        description = "Delete a vaccination record by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Vaccination record deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vaccination record not found",
            content = @Content
        )
    })
    public ResponseEntity<Void> deleteVaccination(@PathVariable UUID id) {
        log.info("DELETE /api/v1/vaccinations/{} - Delete vaccination", id);
        vaccinationService.deleteVaccination(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all vaccination records with pagination.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get all vaccination records",
        description = "Retrieve all vaccination records with pagination support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getAllVaccinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations - Get all vaccinations with pagination");

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getAllVaccinations(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records for a specific livestock animal.
     */
    @GetMapping("/livestock/{livestockId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by livestock",
        description = "Retrieve all vaccination records for a specific livestock animal"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByLivestockId(
            @PathVariable UUID livestockId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/livestock/{} - Get vaccinations by livestock ID", livestockId);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByLivestockId(livestockId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records administered by a specific veterinary officer.
     */
    @GetMapping("/veterinary/{officerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by veterinary officer",
        description = "Retrieve all vaccination records administered by a specific veterinary officer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByVeterinaryOfficerId(
            @PathVariable UUID officerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/veterinary/{} - Get vaccinations by veterinary officer ID", officerId);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByVeterinaryOfficerId(officerId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records of a specific type.
     */
    @GetMapping("/type/{vaccineType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by vaccine type",
        description = "Retrieve all vaccination records of a specific vaccine type"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByVaccineType(
            @PathVariable String vaccineType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/type/{} - Get vaccinations by vaccine type", vaccineType);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByVaccineType(vaccineType, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records within a specific date range.
     */
    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by date range",
        description = "Retrieve all vaccination records administered within a specific date range"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/date-range - Get vaccinations from {} to {}", startDate, endDate);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records with a specific batch number.
     */
    @GetMapping("/batch/{batchNumber}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by batch number",
        description = "Retrieve all vaccination records with a specific batch number"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByBatchNumber(
            @PathVariable String batchNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/batch/{} - Get vaccinations by batch number", batchNumber);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByBatchNumber(batchNumber, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all unverified vaccination records.
     */
    @GetMapping("/unverified")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get unverified vaccinations",
        description = "Retrieve all unverified vaccination records"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getUnverifiedVaccinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/unverified - Get unverified vaccinations");

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getUnverifiedVaccinations(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all verified vaccination records.
     */
    @GetMapping("/verified")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get verified vaccinations",
        description = "Retrieve all verified vaccination records"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVerifiedVaccinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "verifiedDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/verified - Get verified vaccinations");

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVerifiedVaccinations(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records due for booster shots.
     */
    @GetMapping("/due-for-booster")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations due for booster",
        description = "Retrieve all vaccination records due for booster shots by a specific date"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsDueForBooster(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nextVaccinationDate,asc") String[] sort) {
        log.info("GET /api/v1/vaccinations/due-for-booster - Get vaccinations due by {}", dueDate);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsDueForBooster(dueDate, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all vaccination records administered in a specific location.
     */
    @GetMapping("/location/{location}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by location",
        description = "Retrieve all vaccination records administered in a specific location"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/location/{} - Get vaccinations by location", location);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByLocation(location, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify a vaccination record.
     */
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','VETERINARY_OFFICER')")
    @Operation(
        summary = "Verify vaccination record",
        description = "Verify a vaccination record"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination record verified successfully",
            content = @Content(schema = @Schema(implementation = VaccinationResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Vaccination record or officer not found",
            content = @Content
        )
    })
    public ResponseEntity<VaccinationResponse> verifyVaccination(
            @PathVariable UUID id,
            @RequestParam UUID verifiedById) {
        log.info("POST /api/v1/vaccinations/{}/verify - Verify vaccination", id);
        VaccinationResponse response = vaccinationService.verifyVaccination(id, verifiedById);
        return ResponseEntity.ok(response);
    }

    /**
     * Get vaccination statistics.
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','VETERINARY_OFFICER','AGRITEX_OFFICER')")
    @Operation(
        summary = "Get vaccination statistics",
        description = "Get statistics about vaccination records"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content
        )
    })
    public ResponseEntity<zw.co.digistock.dto.response.VaccinationStatistics> getVaccinationStatistics() {
        log.info("GET /api/v1/vaccinations/statistics - Get vaccination statistics");
        zw.co.digistock.dto.response.VaccinationStatistics statistics = vaccinationService.getVaccinationStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get vaccination counts by vaccine type.
     */
    @GetMapping("/statistics/by-type")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','VETERINARY_OFFICER','AGRITEX_OFFICER')")
    @Operation(
        summary = "Get vaccination count by type",
        description = "Get the number of vaccinations per vaccine type"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content
        )
    })
    public ResponseEntity<List<Object[]>> getVaccinationCountByType() {
        log.info("GET /api/v1/vaccinations/statistics/by-type - Get vaccination count by type");
        List<Object[]> statistics = vaccinationService.getVaccinationCountByType();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get vaccination counts by veterinary officer.
     */
    @GetMapping("/statistics/by-veterinary")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','VETERINARY_OFFICER','AGRITEX_OFFICER')")
    @Operation(
        summary = "Get vaccination count by veterinary officer",
        description = "Get the number of vaccinations administered per veterinary officer"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content
        )
    })
    public ResponseEntity<List<Object[]>> getVaccinationCountByVeterinaryOfficer() {
        log.info("GET /api/v1/vaccinations/statistics/by-veterinary - Get vaccination count by veterinary officer");
        List<Object[]> statistics = vaccinationService.getVaccinationCountByVeterinaryOfficer();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get vaccination records for livestock in a specific district.
     */
    @GetMapping("/district/{district}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by district",
        description = "Retrieve all vaccination records for livestock in a specific district"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/district/{} - Get vaccinations by district", district);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByDistrict(district, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get vaccination records for livestock in a specific province.
     */
    @GetMapping("/province/{province}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get vaccinations by province",
        description = "Retrieve all vaccination records for livestock in a specific province"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Vaccination records retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        )
    })
    public ResponseEntity<Page<VaccinationResponse>> getVaccinationsByProvince(
            @PathVariable String province,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "vaccinationDate,desc") String[] sort) {
        log.info("GET /api/v1/vaccinations/province/{} - Get vaccinations by province", province);

        Sort.Order order = new Sort.Order(
            sort[1].equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
            sort[0]
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<VaccinationResponse> response = vaccinationService.getVaccinationsByProvince(province, pageable);
        return ResponseEntity.ok(response);
    }
}
