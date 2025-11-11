package zw.co.digistock.service.biometric;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zw.co.digistock.config.SourceAfisConfig;
import zw.co.digistock.service.storage.MinioStorageService;

import java.io.InputStream;
import java.util.List;

/**
 * Service for biometric fingerprint operations using SourceAFIS.
 * Handles:
 * - Fingerprint template extraction from images
 * - Template matching (1:1 verification and 1:N identification)
 * - Score threshold validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BiometricService {

    private final SourceAfisConfig sourceAfisConfig;
    private final MinioStorageService minioStorageService;

    /**
     * Extract fingerprint template from an image
     *
     * @param imageBytes Raw fingerprint image bytes (PNG, JPG, BMP)
     * @return Serialized fingerprint template as byte array
     */
    public byte[] extractTemplate(byte[] imageBytes) {
        try {
            FingerprintImage fingerprintImage = new FingerprintImage()
                .decode(imageBytes);

            FingerprintTemplate template = new FingerprintTemplate(fingerprintImage);

            byte[] serialized = template.toByteArray();
            log.debug("Extracted fingerprint template, size: {} bytes", serialized.length);
            return serialized;
        } catch (Exception e) {
            log.error("Failed to extract fingerprint template", e);
            throw new RuntimeException("Fingerprint template extraction failed", e);
        }
    }

    /**
     * Extract template from MinIO-stored image
     *
     * @param minioReference MinIO reference to fingerprint image
     * @return Serialized template
     */
    public byte[] extractTemplateFromMinio(String minioReference) {
        try (InputStream inputStream = minioStorageService.downloadFile(minioReference)) {
            byte[] imageBytes = inputStream.readAllBytes();
            return extractTemplate(imageBytes);
        } catch (Exception e) {
            log.error("Failed to extract template from MinIO reference: {}", minioReference, e);
            throw new RuntimeException("Template extraction from MinIO failed", e);
        }
    }

    /**
     * Match a probe template against a candidate template (1:1 verification)
     *
     * @param probeTemplate Template to verify
     * @param candidateTemplate Template to compare against
     * @return Match score (0-100+). Higher = better match.
     */
    public double matchTemplates(byte[] probeTemplate, byte[] candidateTemplate) {
        try {
            FingerprintTemplate probe = new FingerprintTemplate(probeTemplate);
            FingerprintTemplate candidate = new FingerprintTemplate(candidateTemplate);

            FingerprintMatcher matcher = new FingerprintMatcher(probe);
            double score = matcher.match(candidate);

            log.debug("Fingerprint match score: {}", score);
            return score;
        } catch (Exception e) {
            log.error("Failed to match fingerprint templates", e);
            throw new RuntimeException("Fingerprint matching failed", e);
        }
    }

    /**
     * Verify if a probe matches a candidate above the configured threshold
     *
     * @param probeTemplate Probe fingerprint template
     * @param candidateTemplate Candidate fingerprint template
     * @return true if match score >= threshold, false otherwise
     */
    public boolean verifyMatch(byte[] probeTemplate, byte[] candidateTemplate) {
        double score = matchTemplates(probeTemplate, candidateTemplate);
        boolean isMatch = score >= sourceAfisConfig.getMatchThreshold();
        log.debug("Verification result: {} (score: {}, threshold: {})",
                isMatch, score, sourceAfisConfig.getMatchThreshold());
        return isMatch;
    }

    /**
     * Identify which candidate template (if any) matches the probe (1:N identification)
     *
     * @param probeTemplate Probe fingerprint template
     * @param candidateRefs List of MinIO references to candidate templates
     * @return MinIO reference of best match, or null if no match above threshold
     */
    public BiometricMatchResult identifyBestMatch(byte[] probeTemplate, List<String> candidateRefs) {
        try {
            FingerprintTemplate probe = new FingerprintTemplate(probeTemplate);
            FingerprintMatcher matcher = new FingerprintMatcher(probe);

            String bestMatchRef = null;
            double bestScore = 0.0;

            for (String candidateRef : candidateRefs) {
                byte[] candidateBytes = loadTemplate(candidateRef);
                FingerprintTemplate candidate = new FingerprintTemplate(candidateBytes);
                double score = matcher.match(candidate);

                log.debug("Match against {}: score={}", candidateRef, score);

                if (score > bestScore) {
                    bestScore = score;
                    bestMatchRef = candidateRef;
                }
            }

            if (bestScore >= sourceAfisConfig.getMatchThreshold()) {
                log.info("Best match found: {} with score {}", bestMatchRef, bestScore);
                return new BiometricMatchResult(bestMatchRef, bestScore, true);
            } else {
                log.info("No match found above threshold. Best score: {}", bestScore);
                return new BiometricMatchResult(null, bestScore, false);
            }
        } catch (Exception e) {
            log.error("Failed to identify best match", e);
            throw new RuntimeException("Fingerprint identification failed", e);
        }
    }

    /**
     * Load template from MinIO (with caching)
     */
    @Cacheable(value = "fingerprintTemplates", key = "#minioRef", unless = "!@sourceAfisConfig.enableCaching")
    private byte[] loadTemplate(String minioRef) {
        try (InputStream inputStream = minioStorageService.downloadFile(minioRef)) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to load template from MinIO: {}", minioRef, e);
            throw new RuntimeException("Template loading failed", e);
        }
    }

    /**
     * Result object for biometric matching
     */
    public record BiometricMatchResult(
        String matchedReference,
        double score,
        boolean isMatch
    ) {}
}
