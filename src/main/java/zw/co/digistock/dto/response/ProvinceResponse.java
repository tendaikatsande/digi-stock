package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Province responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceResponse {
    private String id;
    private String name;
    private String code;
    private boolean active;
}
