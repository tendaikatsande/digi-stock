package zw.co.digistock.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.digistock.service.storage.MinioStorageService;

/**
 * REST controller for file operations
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final MinioStorageService minioStorageService;

    /**
     * Get presigned URL for a file
     */
    @GetMapping("/signed-url")
    public ResponseEntity<SignedUrlResponse> getSignedUrl(
            @RequestParam("fileRef") String fileRef,
            @RequestParam(value = "expiryMinutes", defaultValue = "60") int expiryMinutes) {
        log.info("GET /api/v1/files/signed-url?fileRef={}&expiryMinutes={}", fileRef, expiryMinutes);

        String signedUrl = minioStorageService.getPresignedUrl(fileRef, expiryMinutes);

        SignedUrlResponse response = new SignedUrlResponse(fileRef, signedUrl, expiryMinutes);
        return ResponseEntity.ok(response);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignedUrlResponse {
        private String fileRef;
        private String signedUrl;
        private int expiryMinutes;
    }
}
