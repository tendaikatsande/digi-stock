package zw.co.digistock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.dto.response.DistrictResponse;
import zw.co.digistock.dto.response.ProvinceResponse;
import zw.co.digistock.dto.response.WardResponse;
import zw.co.digistock.service.DistrictService;
import zw.co.digistock.service.ProvinceService;
import zw.co.digistock.service.WardService;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for administrative locations (provinces, districts, wards)
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Slf4j
public class LocationController {

    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final WardService wardService;

    /**
     * Get all provinces
     * Accessible by all authenticated users
     */
    @GetMapping("/provinces")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProvinceResponse>> getAllProvinces() {
        log.info("GET /api/v1/locations/provinces - Get all provinces");
        List<ProvinceResponse> provinces = provinceService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    /**
     * Get province by ID
     * Accessible by all authenticated users
     */
    @GetMapping("/provinces/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProvinceResponse> getProvinceById(@PathVariable UUID id) {
        log.info("GET /api/v1/locations/provinces/{}", id);
        ProvinceResponse province = provinceService.getProvinceById(id);
        return ResponseEntity.ok(province);
    }

    /**
     * Get all districts
     * Accessible by all authenticated users
     */
    @GetMapping("/districts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DistrictResponse>> getAllDistricts() {
        log.info("GET /api/v1/locations/districts - Get all districts");
        List<DistrictResponse> districts = districtService.getAllDistricts();
        return ResponseEntity.ok(districts);
    }

    /**
     * Get districts by province
     * Accessible by all authenticated users
     */
    @GetMapping("/provinces/{provinceId}/districts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DistrictResponse>> getDistrictsByProvince(@PathVariable UUID provinceId) {
        log.info("GET /api/v1/locations/provinces/{}/districts", provinceId);
        List<DistrictResponse> districts = districtService.getDistrictsByProvince(provinceId);
        return ResponseEntity.ok(districts);
    }

    /**
     * Get district by ID
     * Accessible by all authenticated users
     */
    @GetMapping("/districts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DistrictResponse> getDistrictById(@PathVariable UUID id) {
        log.info("GET /api/v1/locations/districts/{}", id);
        DistrictResponse district = districtService.getDistrictById(id);
        return ResponseEntity.ok(district);
    }

    /**
     * Get all wards
     * Accessible by all authenticated users
     */
    @GetMapping("/wards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WardResponse>> getAllWards() {
        log.info("GET /api/v1/locations/wards - Get all wards");
        List<WardResponse> wards = wardService.getAllWards();
        return ResponseEntity.ok(wards);
    }

    /**
     * Get wards by district
     * Accessible by all authenticated users
     */
    @GetMapping("/districts/{districtId}/wards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WardResponse>> getWardsByDistrict(@PathVariable UUID districtId) {
        log.info("GET /api/v1/locations/districts/{}/wards", districtId);
        List<WardResponse> wards = wardService.getWardsByDistrict(districtId);
        return ResponseEntity.ok(wards);
    }

    /**
     * Get ward by ID
     * Accessible by all authenticated users
     */
    @GetMapping("/wards/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WardResponse> getWardById(@PathVariable UUID id) {
        log.info("GET /api/v1/locations/wards/{}", id);
        WardResponse ward = wardService.getWardById(id);
        return ResponseEntity.ok(ward);
    }
}
