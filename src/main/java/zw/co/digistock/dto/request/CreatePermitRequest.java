package zw.co.digistock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a movement permit
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermitRequest {

    @NotNull(message = "Clearance ID is required")
    private UUID clearanceId;

    @NotNull(message = "Livestock ID is required")
    private UUID livestockId;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    @NotBlank(message = "To location is required")
    private String toLocation;

    private String purpose;

    private String transportMode;

    private String vehicleNumber;

    private String driverName;

    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    @NotNull(message = "Valid until date is required")
    private LocalDate validUntil;

    private Double issueLatitude;

    private Double issueLongitude;
}
