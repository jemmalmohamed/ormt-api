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
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.minio.MinioService;

/**
 * Simple FileController for accessing files stored in MinIO
 * Uses direct file access without complex security checks
 */
@Log4j2
@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class FileController {

    @Autowired
    private MinioService minioService;

    @Value("${minio.bucket}")
    private String bucketName;

    /**
     * Get a file from MinIO by its filename
     * Simple, direct file access approach
     */
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            log.debug("Retrieving file: {}", fileName);
            byte[] fileData = minioService.getFile(fileName);
            
            // Set appropriate content type based on file extension
            String contentType = determineContentType(fileName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // Allow browser caching for better performance
            headers.setCacheControl("public, max-age=86400"); // Cache for 1 day
            
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file {}: {}", fileName, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get a file from MinIO by its URL path
     * Compatibility method for existing URLs in the database
     */
    @GetMapping("/url")
    public ResponseEntity<byte[]> getFileByUrl(String url, HttpServletRequest request) {
        // Extract the filename from the URL
        String fileName = extractFilenameFromUrl(url);
        if (fileName == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            log.debug("Retrieving file by URL: {} -> filename: {}", url, fileName);
            byte[] fileData = minioService.getFile(fileName);
            
            // Set appropriate content type
            String contentType = determineContentType(fileName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setCacheControl("public, max-age=86400"); // Cache for 1 day
            
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error retrieving file from URL {}: {}", url, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Extracts the filename from a MinIO URL
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

    /**
     * Determine the content type based on file extension
     */
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
        } else if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else {
            return "application/octet-stream";
        }
    }
}