package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Ward responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardResponse {
    private String id;
    private String name;
    private String code;
    private String districtId;
    private String districtName;
    private boolean active;
}
