package zw.co.digistock.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for returning vaccination record details in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaccinationResponse {

    private UUID id;

    private String vaccineType;

    private LocalDate vaccinationDate;

    private String batchNumber;

    private LocalDate nextVaccinationDate;

    private String notes;

    private OfficerResponse veterinaryOfficer;

    private LivestockResponse livestock;

    private String location;

    private String gpsCoordinates;

    private String administrationMethod;

    private String manufacturer;

    private String lotNumber;

    private LocalDate vaccineExpiryDate;

    private String dose;

    private String storageTemperature;

    private Boolean isVerified;

    private OfficerResponse verifiedBy;

    private LocalDate verifiedDate;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private String createdBy;

    private String updatedBy;

    /**
     * Check if the vaccination is due for a booster shot.
     */
    public boolean isBoosterDue() {
        return nextVaccinationDate != null && nextVaccinationDate.isBefore(LocalDate.now());
    }

    /**
     * Check if the vaccination is upcoming.
     */
    public boolean isUpcoming() {
        return nextVaccinationDate != null && nextVaccinationDate.isAfter(LocalDate.now());
    }

    /**
     * Get days until next vaccination (if applicable)
     */
    public Integer getDaysUntilNextVaccination() {
        if (nextVaccinationDate == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextVaccinationDate);
    }
}
