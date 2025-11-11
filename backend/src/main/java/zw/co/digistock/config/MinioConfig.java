package zw.co.digistock.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * MinIO Configuration for DigiStock object storage.
 * Stores livestock photos, fingerprint templates, PDFs, and QR codes.
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Map<String, String> buckets;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Get bucket name for livestock photos
     */
    public String getLivestockPhotosBucket() {
        return buckets.get("livestock-photos");
    }

    /**
     * Get bucket name for fingerprint templates
     */
    public String getFingerprintsBucket() {
        return buckets.get("fingerprints");
    }

    /**
     * Get bucket name for movement permits
     */
    public String getPermitsBucket() {
        return buckets.get("permits");
    }

    /**
     * Get bucket name for police clearances
     */
    public String getClearancesBucket() {
        return buckets.get("clearances");
    }

    /**
     * Get bucket name for QR codes
     */
    public String getQrCodesBucket() {
        return buckets.get("qr-codes");
    }
}
