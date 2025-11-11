package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Livestock response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivestockResponse {

    private UUID id;
    private String tagCode;
    private String name;
    private String breed;
    private String sex;
    private LocalDate birthDate;
    private String color;
    private String distinguishingMarks;

    private OwnerSummary owner;
    private LivestockSummary mother;
    private LivestockSummary father;

    private Double registrationLatitude;
    private Double registrationLongitude;

    private boolean stolen;
    private LocalDate stolenDate;

    private List<PhotoInfo> photos;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerSummary {
        private UUID id;
        private String nationalId;
        private String fullName;
        private String phoneNumber;
        private String district;
        private String province;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LivestockSummary {
        private UUID id;
        private String tagCode;
        private String name;
        private String breed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoInfo {
        private UUID id;
        private String photoRef;
        private String description;
        private String photoType;
    }
}
