package zw.co.digistock.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.dto.request.RegisterLivestockRequest;
import zw.co.digistock.dto.response.LivestockResponse;

import java.util.UUID;

/**
 * Service interface for livestock management operations
 */
public interface ILivestockService {

    /**
     * Register new livestock
     */
    LivestockResponse registerLivestock(RegisterLivestockRequest request);

    /**
     * Upload photo for livestock
     */
    LivestockResponse uploadPhoto(UUID livestockId, MultipartFile file, String description, String photoType);

    /**
     * Get livestock by ID
     */
    LivestockResponse getLivestockById(UUID id);

    /**
     * Get livestock by tag code
     */
    LivestockResponse getLivestockByTagCode(String tagCode);

    /**
     * Get all livestock for an owner with pagination
     */
    Page<LivestockResponse> getLivestockByOwner(UUID ownerId, Pageable pageable);

    /**
     * Get offspring of a livestock (by mother or father) with pagination
     */
    Page<LivestockResponse> getOffspring(UUID livestockId, Pageable pageable);

    /**
     * Mark livestock as stolen
     */
    LivestockResponse markAsStolen(UUID livestockId);

    /**
     * Mark livestock as recovered
     */
    LivestockResponse markAsRecovered(UUID livestockId);

    /**
     * Get stolen livestock with pagination
     */
    Page<LivestockResponse> getStolenLivestock(Pageable pageable);

    /**
     * Search livestock with pagination
     */
    Page<LivestockResponse> searchLivestock(String query, Pageable pageable);
}
