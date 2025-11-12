package zw.co.digistock.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic message response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic message response")
public class MessageResponse {

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Success status", example = "true")
    private boolean success;
}
