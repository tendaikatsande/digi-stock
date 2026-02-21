package zw.co.digistock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.digistock.domain.enums.TransferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private UUID id;
    private LivestockResponse.LivestockSummary livestock;
    private LivestockResponse.OwnerSummary fromOwner;
    private LivestockResponse.OwnerSummary toOwner;
    private String initiatedBy;
    private TransferStatus status;
    private LocalDate transferDate;
    private String reason;
    private boolean fromOwnerConfirmed;
    private boolean toOwnerConfirmed;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
