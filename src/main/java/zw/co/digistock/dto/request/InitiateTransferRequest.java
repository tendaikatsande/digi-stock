package zw.co.digistock.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateTransferRequest {

    @NotNull(message = "Livestock ID is required")
    private UUID livestockId;

    @NotNull(message = "New owner ID is required")
    private UUID toOwnerId;

    private String reason;

    private LocalDate transferDate;
}
