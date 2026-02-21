package zw.co.digistock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.OwnershipTransfer;
import zw.co.digistock.domain.enums.TransferStatus;

import java.util.UUID;

@Repository
public interface OwnershipTransferRepository extends JpaRepository<OwnershipTransfer, UUID> {

    Page<OwnershipTransfer> findByLivestockId(UUID livestockId, Pageable pageable);

    Page<OwnershipTransfer> findByFromOwnerId(UUID ownerId, Pageable pageable);

    Page<OwnershipTransfer> findByToOwnerId(UUID ownerId, Pageable pageable);

    Page<OwnershipTransfer> findByStatus(TransferStatus status, Pageable pageable);

    boolean existsByLivestockIdAndStatus(UUID livestockId, TransferStatus status);
}
