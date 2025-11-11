package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for Owner response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {

    private UUID id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String ward;
    private String district;
    private String province;
    private String photoRef;
    private boolean biometricEnrolled;
    private int livestockCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
