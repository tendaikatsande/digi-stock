package zw.co.digistock.service.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zw.co.digistock.config.MinioConfig;
import zw.co.digistock.service.storage.MinioStorageService;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for generating QR codes for:
 * - Movement permits
 * - Police clearances
 * - Livestock tags
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private final MinioStorageService minioStorageService;
    private final MinioConfig minioConfig;

    @Value("${digistock.qr.size:300}")
    private int qrCodeSize;

    /**
     * Generate a QR code from text and upload to MinIO
     *
     * @param content Text content to encode in QR code
     * @param entityType Type of entity (permit, clearance, livestock)
     * @param entityId ID of the entity
     * @return MinIO reference to the generated QR code image
     */
    public String generateAndUploadQrCode(String content, String entityType, String entityId) {
        try {
            byte[] qrCodeBytes = generateQrCodeImage(content);

            String objectName = String.format("%s/%s/%s.png", entityType, entityId, UUID.randomUUID());
            String bucketName = minioConfig.getQrCodesBucket();

            String reference = minioStorageService.uploadBytes(
                qrCodeBytes,
                bucketName,
                objectName,
                "image/png"
            );

            log.info("Generated QR code for {} {}: {}", entityType, entityId, reference);
            return reference;
        } catch (Exception e) {
            log.error("Failed to generate QR code for {} {}", entityType, entityId, e);
            throw new RuntimeException("QR code generation failed", e);
        }
    }

    /**
     * Generate QR code image bytes from text content
     *
     * @param content Text to encode
     * @return PNG image bytes
     */
    private byte[] generateQrCodeImage(String content) throws WriterException, java.io.IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
            content,
            BarcodeFormat.QR_CODE,
            qrCodeSize,
            qrCodeSize,
            hints
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Generate QR code for a movement permit
     * Format: PERMIT:{permitNumber}:{livestockTag}:{validUntil}
     */
    public String generatePermitQrCode(String permitNumber, String livestockTag, String validUntil, String permitId) {
        String content = String.format("PERMIT:%s:%s:%s", permitNumber, livestockTag, validUntil);
        return generateAndUploadQrCode(content, "permits", permitId);
    }

    /**
     * Generate QR code for a police clearance
     * Format: CLEARANCE:{clearanceNumber}:{livestockTag}:{expiryDate}
     */
    public String generateClearanceQrCode(String clearanceNumber, String livestockTag, String expiryDate, String clearanceId) {
        String content = String.format("CLEARANCE:%s:%s:%s", clearanceNumber, livestockTag, expiryDate);
        return generateAndUploadQrCode(content, "clearances", clearanceId);
    }

    /**
     * Generate QR code for a livestock tag
     * Format: LIVESTOCK:{tagCode}:{ownerId}
     */
    public String generateLivestockQrCode(String tagCode, String ownerId, String livestockId) {
        String content = String.format("LIVESTOCK:%s:%s", tagCode, ownerId);
        return generateAndUploadQrCode(content, "livestock", livestockId);
    }
}
