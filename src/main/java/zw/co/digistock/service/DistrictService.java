package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.District;
import zw.co.digistock.dto.response.DistrictResponse;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.DistrictRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for district management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistrictService {

    private final DistrictRepository districtRepository;

    /**
     * Get all districts
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "districts")
    public List<DistrictResponse> getAllDistricts() {
        log.info("Fetching all districts");
        return districtRepository.findByActiveTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get districts by province
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "districtsByProvince", key = "#provinceId")
    public List<DistrictResponse> getDistrictsByProvince(UUID provinceId) {
        log.info("Fetching districts for province: {}", provinceId);
        return districtRepository.findByProvinceId(provinceId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get district by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "districts", key = "#id")
    public DistrictResponse getDistrictById(UUID id) {
        log.info("Fetching district: {}", id);
        District district = districtRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("District", "id", id));
        return mapToResponse(district);
    }

    /**
     * Map District entity to response DTO
     */
    private DistrictResponse mapToResponse(District district) {
        return DistrictResponse.builder()
            .id(district.getId().toString())
            .name(district.getName())
            .code(district.getCode())
            .provinceId(district.getProvince().getId().toString())
            .provinceName(district.getProvince().getName())
            .active(district.isActive())
            .build();
    }
}
