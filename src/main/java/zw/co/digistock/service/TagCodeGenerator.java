package zw.co.digistock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.digistock.repository.LivestockRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating unique livestock tag codes
 * Format: {PROVINCE}-{DISTRICT}-{WARD}-{SERIAL}
 * Example: HA-02-012-0234
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagCodeGenerator {

    private final LivestockRepository livestockRepository;

    /**
     * Province code mappings
     */
    private static final Map<String, String> PROVINCE_CODES = Map.of(
        "Bulawayo", "BW",
        "Harare", "HA",
        "Manicaland", "MA",
        "Mashonaland Central", "MC",
        "Mashonaland East", "ME",
        "Mashonaland West", "MW",
        "Masvingo", "MV",
        "Matabeleland North", "MN",
        "Matabeleland South", "MS",
        "Midlands", "ML"
    );

    /**
     * Generate tag code based on location (province, district, ward)
     *
     * @param province Province name
     * @param districtCode District code (2 digits)
     * @param wardCode Ward code (3 digits)
     * @return Generated tag code
     */
    public String generateTagCode(String province, String districtCode, String wardCode) {
        String provinceCode = PROVINCE_CODES.getOrDefault(province, "XX");

        // Format district and ward codes with leading zeros
        String formattedDistrict = String.format("%02d", Integer.parseInt(districtCode));
        String formattedWard = String.format("%03d", Integer.parseInt(wardCode));

        // Find next available serial number for this province-district-ward combination
        String prefix = String.format("%s-%s-%s-", provinceCode, formattedDistrict, formattedWard);
        int serial = getNextSerial(prefix);
        String formattedSerial = String.format("%04d", serial);

        String tagCode = prefix + formattedSerial;
        log.debug("Generated tag code: {}", tagCode);

        return tagCode;
    }

    /**
     * Get next available serial number for a given prefix
     */
    private int getNextSerial(String prefix) {
        // Find all tag codes with this prefix
        String pattern = prefix + "%";
        var existingTags = livestockRepository.findByTagCodePattern(pattern);

        if (existingTags.isEmpty()) {
            return 1; // First animal in this ward
        }

        // Extract serial numbers and find the maximum
        int maxSerial = existingTags.stream()
            .map(livestock -> livestock.getTagCode().substring(prefix.length()))
            .mapToInt(Integer::parseInt)
            .max()
            .orElse(0);

        return maxSerial + 1;
    }

    /**
     * Validate tag code format
     *
     * @param tagCode Tag code to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidTagCode(String tagCode) {
        if (tagCode == null || tagCode.isEmpty()) {
            return false;
        }

        // Format: XX-DD-WWW-SSSS (2-2-3-4 pattern)
        String pattern = "^[A-Z]{2}-\\d{2}-\\d{3}-\\d{4}$";
        return tagCode.matches(pattern);
    }

    /**
     * Extract province code from tag code
     */
    public String extractProvinceCode(String tagCode) {
        if (!isValidTagCode(tagCode)) {
            throw new IllegalArgumentException("Invalid tag code format");
        }
        return tagCode.substring(0, 2);
    }

    /**
     * Extract district code from tag code
     */
    public String extractDistrictCode(String tagCode) {
        if (!isValidTagCode(tagCode)) {
            throw new IllegalArgumentException("Invalid tag code format");
        }
        return tagCode.substring(3, 5);
    }

    /**
     * Extract ward code from tag code
     */
    public String extractWardCode(String tagCode) {
        if (!isValidTagCode(tagCode)) {
            throw new IllegalArgumentException("Invalid tag code format");
        }
        return tagCode.substring(6, 9);
    }

    /**
     * Extract serial number from tag code
     */
    public String extractSerial(String tagCode) {
        if (!isValidTagCode(tagCode)) {
            throw new IllegalArgumentException("Invalid tag code format");
        }
        return tagCode.substring(10, 14);
    }
}
