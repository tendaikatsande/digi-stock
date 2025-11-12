package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.Province;
import zw.co.digistock.dto.response.ProvinceResponse;
import zw.co.digistock.exception.ResourceNotFoundException;
import zw.co.digistock.repository.ProvinceRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for province management operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProvinceService {

    private final ProvinceRepository provinceRepository;

    /**
     * Get all provinces
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "provinces")
    public List<ProvinceResponse> getAllProvinces() {
        log.info("Fetching all provinces");
        return provinceRepository.findByActiveTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get province by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "provinces", key = "#id")
    public ProvinceResponse getProvinceById(UUID id) {
        log.info("Fetching province: {}", id);
        Province province = provinceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Province", "id", id));
        return mapToResponse(province);
    }

    /**
     * Map Province entity to response DTO
     */
    private ProvinceResponse mapToResponse(Province province) {
        return ProvinceResponse.builder()
            .id(province.getId().toString())
            .name(province.getName())
            .code(province.getCode())
            .active(province.isActive())
            .build();
    }
}
