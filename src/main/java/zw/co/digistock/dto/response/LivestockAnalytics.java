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
public class LivestockAnalytics {
    private long totalLivestock;
    private long totalOwners;
    private long stolenCount;
    private Map<String, Long> byProvince;
    private Map<String, Long> byBreed;
}
