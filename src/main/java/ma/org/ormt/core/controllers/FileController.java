package ma.org.ormt.core.controllers;

import java.util.HashMap;
import java.util.Map;

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

import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.minio.MinioService;
import ma.org.ormt.core.services.FileSecurityService;

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
    /**
     * Generate a secure URL for accessing a file, including a time-limited token
     */
    @GetMapping("/secure-url/{fileName}")
    public ResponseEntity<Map<String, Object>> getSecureFileUrl(@PathVariable String fileName) {
        String token = fileSecurityService.generateFileToken(fileName, null);
        if (token.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String encodedToken = java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);
        // Fix: Use the current server's base URL instead of frontend URL for API
        // endpoints
        String secureUrl = "/api/v1/files/" + fileName + "?token=" + encodedToken;
        long expirySeconds = 100;
        Map<String, Object> response = new HashMap<>();
        response.put("url", secureUrl);
        response.put("expiresIn", expirySeconds);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            byte[] fileData = minioService.getMinioFile(fileName);
            String contentType = determineContentType(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("private, max-age=5"); // Cache for token lifetime
            headers.setPragma(""); // Remove no-cache
            headers.setExpires(System.currentTimeMillis() + 5_000); // 5 seconds
            headers.add("Content-Security-Policy", "default-src 'self'");
            headers.add("ETag", generateContentETag(fileData));
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file {}: {}", fileName, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Generate a deterministic ETag based on file content
    private String generateContentETag(byte[] fileData) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(fileData);
            StringBuilder sb = new StringBuilder("\"");
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            sb.append("\"");
            return sb.toString();
        } catch (Exception e) {
            return "\"" + java.util.UUID.randomUUID().toString() + "\"";
        }
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