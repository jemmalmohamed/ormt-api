package ma.org.ormt.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.services.FileSecurityService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = "${keycloak.clients.frontend.root-url}", allowCredentials = "true")
public class FileController {

    @Autowired
    private MinioService minioService;

    @Autowired
    private FileSecurityService fileSecurityService;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${keycloak.clients.frontend.root-url}")
    private String frontendUrl;

    /**
     * Generate a secure URL for accessing a file, including a time-limited token
     */
    @GetMapping("/secure-url/{fileName}")
    public ResponseEntity<String> getSecureFileUrl(@PathVariable String fileName) {
        // Generate a secure token (no browser fingerprint)
        String token = fileSecurityService.generateFileToken(fileName, null);
        if (token.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String encodedToken = java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
        String secureUrl = "/files/" + fileName + "?token=" + encodedToken;
        return new ResponseEntity<>(secureUrl, HttpStatus.OK);
    }

    /**
     * Get a file from MinIO by its filename with token verification
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable String fileName,
            @RequestParam(required = true) String token) {
        // Decode the token if present (it may be URL-encoded)
        String decodedToken = null;
        if (token != null) {
            try {
                decodedToken = java.net.URLDecoder.decode(token, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Error decoding token: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            log.warn("No token provided for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Only check token validity (no browser fingerprint)
        if (!fileSecurityService.validateFileToken(fileName, decodedToken, null)) {
            log.warn("Invalid or expired token for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            byte[] fileData = minioService.getFile(fileName);
            String contentType = determineContentType(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.add("Content-Security-Policy", "default-src 'self'");
            headers.add("ETag", generateRandomETag());
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file {}: {}", fileName, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get a file from MinIO by its complete URL path
     * This allows compatibility with existing URLs stored in the database
     */
    @GetMapping("/url")
    public ResponseEntity<byte[]> getFileByUrl(
            @RequestParam("url") String url,
            @RequestParam(required = true) String token) {
        String fileName = extractFilenameFromUrl(url);
        if (fileName == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String decodedToken = null;
        if (token != null) {
            try {
                decodedToken = java.net.URLDecoder.decode(token, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Error decoding token: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            log.warn("No token provided for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (!fileSecurityService.validateFileToken(fileName, decodedToken, null)) {
            log.warn("Invalid or expired token for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            log.debug("Extracted filename: {} from URL: {}", fileName, url);
            byte[] fileData = minioService.getFile(fileName);
            String contentType = determineContentType(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.add("Content-Security-Policy", "default-src 'self'");
            headers.add("ETag", generateRandomETag());
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file from URL {}: {}", url, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Generate a random ETag to prevent caching
    private String generateRandomETag() {
        return "\"" + java.util.UUID.randomUUID().toString() + "\"";
    }

    /**
     * Extracts the filename from a MinIO URL
     * Handles URLs like http://localhost:8093/bucket-name/filename.jpg
     */
    private String extractFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Check if it contains the bucket name
        int bucketIndex = url.indexOf("/" + bucketName + "/");
        if (bucketIndex != -1) {
            // Extract everything after the bucket name
            return url.substring(bucketIndex + bucketName.length() + 2);
        }

        // If not found with the bucket pattern, try to get the last segment of the URL
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }

        return null;
    }

    private String determineContentType(String fileName) {
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            return "application/msword";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else {
            return "application/octet-stream";
        }
    }
}