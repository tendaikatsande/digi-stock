package zw.co.digistock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for SourceAFIS biometric fingerprint matching
 */
@Configuration
@ConfigurationProperties(prefix = "sourceafis")
@Data
public class SourceAfisConfig {

    /**
     * Matching threshold score (0-100).
     * Higher values = stricter matching, lower false acceptance rate.
     * Recommended: 40-60 for most use cases.
     */
    private double matchThreshold = 40.0;

    /**
     * Whether to enable template caching for faster matching
     */
    private boolean enableCaching = true;

    /**
     * Maximum number of templates to cache in memory
     */
    private int cacheSize = 1000;
}
