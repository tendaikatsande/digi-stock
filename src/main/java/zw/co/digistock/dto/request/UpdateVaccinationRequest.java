package zw.co.digistock.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for updating an existing vaccination record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVaccinationRequest {

    @Size(max = 255, message = "Vaccine type must be less than 255 characters")
    private String vaccineType;

    @PastOrPresent(message = "Vaccination date cannot be in the future")
    private LocalDate vaccinationDate;

    @Size(max = 100, message = "Batch number must be less than 100 characters")
    private String batchNumber;

    private LocalDate nextVaccinationDate;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    private UUID veterinaryOfficerId;

    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;

    private String gpsCoordinates;

    @Size(max = 100, message = "Administration method must be less than 100 characters")
    private String administrationMethod;

    @Size(max = 255, message = "Manufacturer must be less than 255 characters")
    private String manufacturer;

    @Size(max = 100, message = "Lot number must be less than 100 characters")
    private String lotNumber;

    private LocalDate vaccineExpiryDate;

    @Size(max = 50, message = "Dose must be less than 50 characters")
    private String dose;

    @Size(max = 50, message = "Storage temperature must be less than 50 characters")
    private String storageTemperature;

    private Boolean isVerified;

    private UUID verifiedById;

    private LocalDate verifiedDate;
}
