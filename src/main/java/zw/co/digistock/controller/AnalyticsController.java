package zw.co.digistock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zw.co.digistock.dto.response.ClearanceAnalytics;
import zw.co.digistock.dto.response.DashboardSummary;
import zw.co.digistock.dto.response.LivestockAnalytics;
import zw.co.digistock.dto.response.PermitAnalytics;
import zw.co.digistock.service.AnalyticsService;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Admin dashboard and reporting endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN')")
    @Operation(summary = "Get dashboard summary", description = "Returns aggregated livestock, permit, clearance and vaccination statistics")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        log.info("GET /api/v1/analytics/dashboard");
        return ResponseEntity.ok(analyticsService.getDashboardSummary());
    }

    @GetMapping("/livestock")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','AGRITEX_OFFICER')")
    @Operation(summary = "Get livestock analytics", description = "Returns livestock counts by province and breed")
    public ResponseEntity<LivestockAnalytics> getLivestockAnalytics() {
        log.info("GET /api/v1/analytics/livestock");
        return ResponseEntity.ok(analyticsService.getLivestockAnalytics());
    }

    @GetMapping("/permits")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','POLICE_OFFICER')")
    @Operation(summary = "Get permit analytics", description = "Returns permit counts by status and this month's issuance count")
    public ResponseEntity<PermitAnalytics> getPermitAnalytics() {
        log.info("GET /api/v1/analytics/permits");
        return ResponseEntity.ok(analyticsService.getPermitAnalytics());
    }

    @GetMapping("/clearances")
    @PreAuthorize("hasAnyRole('ADMIN','NATIONAL_ADMIN','PROVINCIAL_ADMIN','DISTRICT_ADMIN','POLICE_OFFICER')")
    @Operation(summary = "Get clearance analytics", description = "Returns clearance counts by status")
    public ResponseEntity<ClearanceAnalytics> getClearanceAnalytics() {
        log.info("GET /api/v1/analytics/clearances");
        return ResponseEntity.ok(analyticsService.getClearanceAnalytics());
    }
}
