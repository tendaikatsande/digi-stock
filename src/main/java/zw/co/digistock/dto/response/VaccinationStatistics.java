package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for vaccination statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationStatistics {

    private long totalVaccinations;

    private long verifiedVaccinations;

    private long unverifiedVaccinations;

    private long dueForBooster;

    /**
     * Calculate the verification rate as a percentage.
     *
     * @return Verification rate percentage
     */
    public double getVerificationRate() {
        if (totalVaccinations == 0) {
            return 0;
        }
        return ((double) verifiedVaccinations / totalVaccinations) * 100;
    }

    /**
     * Calculate the percentage of vaccinations due for booster.
     *
     * @return Booster due percentage
     */
    public double getBoosterDueRate() {
        if (totalVaccinations == 0) {
            return 0;
        }
        return ((double) dueForBooster / totalVaccinations) * 100;
    }
}
