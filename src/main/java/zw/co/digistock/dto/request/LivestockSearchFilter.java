package zw.co.digistock.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Filter criteria for livestock search with pagination
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Livestock search filter criteria")
public class LivestockSearchFilter {

    @Schema(description = "Search term for tag code (partial match)", example = "ZW-HR")
    private String tagCode;

    @Schema(description = "Search term for livestock name (partial match)", example = "Bessie")
    private String name;

    @Schema(description = "Breed filter (exact match)", example = "Brahman")
    private String breed;

    @Schema(description = "Gender filter", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Owner UUID filter")
    private UUID ownerId;

    @Schema(description = "District filter", example = "Harare")
    private String district;

    @Schema(description = "Province filter", example = "Harare")
    private String province;

    @Schema(description = "Stolen status filter", example = "false")
    private Boolean stolen;

    @Schema(description = "Date of birth from (inclusive)", example = "2020-01-01")
    private LocalDate dateOfBirthFrom;

    @Schema(description = "Date of birth to (inclusive)", example = "2023-12-31")
    private LocalDate dateOfBirthTo;

    @Schema(description = "Has mother (true/false/null for all)")
    private Boolean hasMother;

    @Schema(description = "Has father (true/false/null for all)")
    private Boolean hasFather;
}
