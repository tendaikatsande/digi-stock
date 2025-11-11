package zw.co.digistock.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a police clearance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClearanceRequest {

    @NotNull(message = "Livestock ID is required")
    private UUID livestockId;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    private String notes;

    private Double issueLatitude;

    private Double issueLongitude;
}
