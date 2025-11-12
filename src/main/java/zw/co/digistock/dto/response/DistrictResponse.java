package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for District responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponse {
    private String id;
    private String name;
    private String code;
    private String provinceId;
    private String provinceName;
    private boolean active;
}
