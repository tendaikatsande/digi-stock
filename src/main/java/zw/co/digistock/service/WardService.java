package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.Ward;
import zw.co.digistock.dto.response.WardResponse;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.WardRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for ward management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WardService {

    private final WardRepository wardRepository;

    /**
     * Get all wards
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "wards")
    public List<WardResponse> getAllWards() {
        log.info("Fetching all wards");
        return wardRepository.findByActiveTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get wards by district
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "wardsByDistrict", key = "#districtId")
    public List<WardResponse> getWardsByDistrict(UUID districtId) {
        log.info("Fetching wards for district: {}", districtId);
        return wardRepository.findByDistrictId(districtId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get ward by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "wards", key = "#id")
    public WardResponse getWardById(UUID id) {
        log.info("Fetching ward: {}", id);
        Ward ward = wardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ward", "id", id));
        return mapToResponse(ward);
    }

    /**
     * Map Ward entity to response DTO
     */
    private WardResponse mapToResponse(Ward ward) {
        return WardResponse.builder()
            .id(ward.getId().toString())
            .name(ward.getName())
            .code(ward.getCode())
            .districtId(ward.getDistrict().getId().toString())
            .districtName(ward.getDistrict().getName())
            .active(ward.isActive())
            .build();
    }
}
