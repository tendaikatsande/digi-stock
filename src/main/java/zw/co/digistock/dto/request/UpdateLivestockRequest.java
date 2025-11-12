package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.Gender;

import java.time.LocalDate;

/**
 * Request DTO for updating livestock information
 * Note: Tag code, owner, and parentage cannot be changed after registration
 * Only descriptive fields can be updated to correct data entry errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update livestock information (limited fields)")
public class UpdateLivestockRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    @Schema(description = "Livestock name or identification", example = "Bessie")
    private String name;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    @Schema(description = "Breed of livestock", example = "Brahman")
    private String breed;

    @Schema(description = "Sex of livestock", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Date of birth", example = "2020-05-15")
    private LocalDate birthDate;

    @Size(max = 100, message = "Color must not exceed 100 characters")
    @Schema(description = "Color or coat description", example = "Brown with white patches")
    private String color;

    @Size(max = 500, message = "Distinguishing marks must not exceed 500 characters")
    @Schema(description = "Distinctive features or markings", example = "White star on forehead, scar on left flank")
    private String distinguishingMarks;
}
