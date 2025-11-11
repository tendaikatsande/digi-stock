package zw.co.digistock.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for DigiStock application
 *
 * Enables caching for frequently accessed data to improve performance.
 * Uses in-memory caching with ConcurrentMapCacheManager.
 *
 * For production, consider using Redis or Caffeine for distributed caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure cache manager with predefined cache names
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "owners",           // Owner entities cache
            "livestock",        // Livestock entities cache
            "clearances",       // Police clearances cache
            "permits",          // Movement permits cache
            "officers",         // Officer entities cache
            "ownerPages",       // Paginated owner results cache
            "livestockPages",   // Paginated livestock results cache
            "clearancePages",   // Paginated clearance results cache
            "permitPages"       // Paginated permit results cache
        );
    }
}
