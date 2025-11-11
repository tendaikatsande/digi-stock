package zw.co.digistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zw.co.digistock.domain.LivestockPhoto;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LivestockPhoto entity operations
 */
@Repository
public interface LivestockPhotoRepository extends JpaRepository<LivestockPhoto, UUID> {

    /**
     * Find all photos for a specific livestock
     */
    List<LivestockPhoto> findByLivestockId(UUID livestockId);

    /**
     * Find photos by type
     */
    List<LivestockPhoto> findByPhotoType(String photoType);

    /**
     * Find photos by livestock and type
     */
    List<LivestockPhoto> findByLivestockIdAndPhotoType(UUID livestockId, String photoType);

    /**
     * Delete all photos for a livestock
     */
    void deleteByLivestockId(UUID livestockId);
}
