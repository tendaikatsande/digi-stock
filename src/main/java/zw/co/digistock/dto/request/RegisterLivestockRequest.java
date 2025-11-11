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
 * DTO for registering new livestock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLivestockRequest {

    @NotBlank(message = "Tag code is required")
    private String tagCode;

    private String name;

    private String breed;

    private String sex;

    private LocalDate birthDate;

    private String color;

    private String distinguishingMarks;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;

    private UUID motherId;

    private UUID fatherId;

    private Double registrationLatitude;

    private Double registrationLongitude;
}
