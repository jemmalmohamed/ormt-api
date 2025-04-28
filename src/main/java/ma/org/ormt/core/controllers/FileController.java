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
    public ResponseEntity<String> getSecureFileUrl(@PathVariable String fileName, HttpServletRequest request) {
        // Check if request is coming from the allowed frontend origin
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if (!isValidOrigin(origin, referer)) {
            log.warn("Unauthorized secure URL generation attempt from origin: {}, referer: {}", origin, referer);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Generate browser fingerprint from User-Agent and IP address
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String browserFingerprint = createBrowserFingerprint(userAgent, ipAddress);

        // Generate a secure token with a shorter validity period (e.g., 2 minutes)
        // This will force re-generation of tokens more frequently
        String token = fileSecurityService.generateFileToken(fileName, browserFingerprint);
        if (token.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // URL encode the token to avoid invalid URL characters like '|'
        String encodedToken = java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);

        // Create the secure URL with the encoded token
        String secureUrl = "/files/" + fileName + "?token=" + encodedToken;
        log.debug("Generated secure URL: {}", secureUrl);
        return new ResponseEntity<>(secureUrl, HttpStatus.OK);
    }

    /**
     * Get a file from MinIO by its filename with token verification
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(
            @PathVariable String fileName,
            @RequestParam(required = true) String token, // Make token REQUIRED
            HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if (!isValidOrigin(origin, referer)) {
            log.warn("Unauthorized secure URL generation attempt from origin: {}, referer: {}", origin, referer);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

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
            // Token is now required
            log.warn("No token provided for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Get browser fingerprint for validation
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String browserFingerprint = createBrowserFingerprint(userAgent, ipAddress);

        // Always require valid token with matching fingerprint
        if (!fileSecurityService.validateFileToken(fileName, decodedToken, browserFingerprint)) {
            log.warn("Invalid or expired token for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            byte[] fileData = minioService.getFile(fileName);

            // Set appropriate content type based on file extension
            String contentType = determineContentType(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // Prevent ALL caching
            headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            // Set strict security headers
            headers.add("Content-Security-Policy", "default-src 'self'");

            // Add a random nonce to break any caching
            headers.add("ETag", generateRandomETag());

            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file {}: {}", fileName, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Generate a random ETag to prevent caching
    private String generateRandomETag() {
        return "\"" + java.util.UUID.randomUUID().toString() + "\"";
    }

    /**
     * Get a file from MinIO by its complete URL path
     * This allows compatibility with existing URLs stored in the database
     */
    @GetMapping("/url")
    public ResponseEntity<byte[]> getFileByUrl(
            @RequestParam("url") String url,
            @RequestParam(required = true) String token, // Make token REQUIRED
            HttpServletRequest request) {

        // Extract the filename from the URL
        String fileName = extractFilenameFromUrl(url);
        if (fileName == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

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
            // Token is now required
            log.warn("No token provided for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Get browser fingerprint for validation
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);
        String browserFingerprint = createBrowserFingerprint(userAgent, ipAddress);

        // Always require valid token with matching fingerprint
        if (!fileSecurityService.validateFileToken(fileName, decodedToken, browserFingerprint)) {
            log.warn("Invalid or expired token for file: {}", fileName);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            log.debug("Extracted filename: {} from URL: {}", fileName, url);

            // Get the file using the extracted filename
            byte[] fileData = minioService.getFile(fileName);

            // Set appropriate content type
            String contentType = determineContentType(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // Prevent ALL caching
            headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            // Set strict security headers
            headers.add("Content-Security-Policy", "default-src 'self'");

            // Add a random nonce to break any caching
            headers.add("ETag", generateRandomETag());

            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file from URL {}: {}", url, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Validates if the request comes from an allowed origin
     * Checks both Origin and Referer headers
     */
    private boolean isValidOrigin(String origin, String referer) {
        // For direct image requests in browser (no Origin but has token)
        // This happens specifically with <img> tags in HTML
        if (origin == null && referer == null) {
            log.debug("Direct image request with no origin/referer - will verify via token");
            // Let the token validation handle security in this case
            return true;
        }
        
        // Otherwise, verify against our frontend URL
        return (origin != null && origin.equals(frontendUrl)) ||
                (referer != null && referer.startsWith(frontendUrl));
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

    /**
     * Gets the client's real IP address, taking into account proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // In case of multiple proxies, the first IP is the client's
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    /**
     * Creates a browser fingerprint based on User-Agent and IP
     */
    private String createBrowserFingerprint(String userAgent, String ipAddress) {
        // Create a simple fingerprint combining User-Agent and IP
        String fingerprint = (userAgent != null ? userAgent : "") + "|" + (ipAddress != null ? ipAddress : "");

        try {
            // Create a hash of the fingerprint for better security
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fingerprint.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error creating browser fingerprint: {}", e.getMessage(), e);
            return fingerprint; // Fallback to plain fingerprint
        }
    }
}