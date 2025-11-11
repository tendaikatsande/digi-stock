package zw.co.digistock.service.storage;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zw.co.digistock.config.MinioConfig;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing file storage in MinIO.
 * Handles upload, download, and signed URL generation for:
 * - Livestock photos
 * - Fingerprint templates
 * - PDF documents (clearances, permits)
 * - QR codes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * Initialize MinIO buckets on startup
     */
    @PostConstruct
    public void init() {
        try {
            createBucketIfNotExists(minioConfig.getLivestockPhotosBucket());
            createBucketIfNotExists(minioConfig.getFingerprintsBucket());
            createBucketIfNotExists(minioConfig.getPermitsBucket());
            createBucketIfNotExists(minioConfig.getClearancesBucket());
            createBucketIfNotExists(minioConfig.getQrCodesBucket());
            log.info("MinIO buckets initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize MinIO buckets", e);
            throw new RuntimeException("MinIO initialization failed", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            );
            log.info("Created MinIO bucket: {}", bucketName);
        }
    }

    /**
     * Upload a file to MinIO and return the object reference
     *
     * @param file Multipart file to upload
     * @param bucketName Target bucket
     * @param folder Optional folder/prefix within bucket
     * @return MinIO reference string (format: minio://{bucket}/{objectName})
     */
    public String uploadFile(MultipartFile file, String bucketName, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String objectName = (folder != null ? folder + "/" : "") + UUID.randomUUID() + extension;

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            String reference = "minio://" + bucketName + "/" + objectName;
            log.debug("Uploaded file to MinIO: {}", reference);
            return reference;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO bucket: {}", bucketName, e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    /**
     * Upload raw bytes (e.g., fingerprint templates, generated QR codes)
     */
    public String uploadBytes(byte[] data, String bucketName, String objectName, String contentType) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new java.io.ByteArrayInputStream(data), data.length, -1)
                    .contentType(contentType)
                    .build()
            );

            String reference = "minio://" + bucketName + "/" + objectName;
            log.debug("Uploaded bytes to MinIO: {}", reference);
            return reference;
        } catch (Exception e) {
            log.error("Failed to upload bytes to MinIO bucket: {}", bucketName, e);
            throw new RuntimeException("Byte upload failed", e);
        }
    }

    /**
     * Download a file from MinIO
     *
     * @param reference MinIO reference (format: minio://{bucket}/{objectName})
     * @return InputStream of the file
     */
    public InputStream downloadFile(String reference) {
        try {
            String[] parts = parseMinioReference(reference);
            String bucketName = parts[0];
            String objectName = parts[1];

            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to download file from MinIO: {}", reference, e);
            throw new RuntimeException("File download failed", e);
        }
    }

    /**
     * Generate a presigned GET URL for temporary access to a file
     *
     * @param reference MinIO reference
     * @param expiryMinutes How long the URL should be valid (in minutes)
     * @return Presigned URL
     */
    public String getPresignedUrl(String reference, int expiryMinutes) {
        try {
            String[] parts = parseMinioReference(reference);
            String bucketName = parts[0];
            String objectName = parts[1];

            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expiryMinutes, TimeUnit.MINUTES)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for: {}", reference, e);
            throw new RuntimeException("Presigned URL generation failed", e);
        }
    }

    /**
     * Delete a file from MinIO
     */
    public void deleteFile(String reference) {
        try {
            String[] parts = parseMinioReference(reference);
            String bucketName = parts[0];
            String objectName = parts[1];

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );

            log.debug("Deleted file from MinIO: {}", reference);
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", reference, e);
            throw new RuntimeException("File deletion failed", e);
        }
    }

    /**
     * Parse MinIO reference string
     *
     * @param reference Format: minio://{bucket}/{objectName}
     * @return Array with [bucketName, objectName]
     */
    private String[] parseMinioReference(String reference) {
        if (reference == null || !reference.startsWith("minio://")) {
            throw new IllegalArgumentException("Invalid MinIO reference: " + reference);
        }

        String path = reference.substring("minio://".length());
        int firstSlash = path.indexOf('/');
        if (firstSlash == -1) {
            throw new IllegalArgumentException("Invalid MinIO reference format: " + reference);
        }

        String bucketName = path.substring(0, firstSlash);
        String objectName = path.substring(firstSlash + 1);
        return new String[]{bucketName, objectName};
    }
}
