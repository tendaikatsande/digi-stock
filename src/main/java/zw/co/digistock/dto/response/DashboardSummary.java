package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private LivestockAnalytics livestock;
    private PermitAnalytics permits;
    private ClearanceAnalytics clearances;
    private VaccinationStatistics vaccinations;
}
