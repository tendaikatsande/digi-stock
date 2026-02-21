package zw.co.digistock.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.domain.enums.TransferStatus;
import zw.co.digistock.dto.request.InitiateTransferRequest;
import zw.co.digistock.dto.response.TransferResponse;

import java.util.UUID;

public interface IOwnershipTransferService {

    TransferResponse initiateTransfer(InitiateTransferRequest request, UUID officerId);

    TransferResponse confirmByCurrentOwner(UUID transferId, MultipartFile fingerprintFile);

    TransferResponse confirmByNewOwner(UUID transferId, MultipartFile fingerprintFile);

    TransferResponse completeTransfer(UUID transferId, UUID officerId);

    TransferResponse cancelTransfer(UUID transferId, UUID officerId, String reason);

    TransferResponse getById(UUID transferId);

    Page<TransferResponse> getByLivestock(UUID livestockId, Pageable pageable);

    Page<TransferResponse> getByStatus(TransferStatus status, Pageable pageable);
}
