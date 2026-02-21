package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitAnalytics {
    private long totalPermits;
    private Map<String, Long> byStatus;
    private long issuedThisMonth;
}
