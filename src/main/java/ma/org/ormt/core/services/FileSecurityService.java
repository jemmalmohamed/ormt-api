package ma.org.ormt.core.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class FileSecurityService {

    @Value("${file.token.secret:changeme}")
    private String tokenSecret;

    @Value("${file.token.validitySeconds:300}")
    private int tokenValiditySeconds;

    /**
     * Generates a secure, time-limited token for file access.
     * The token is valid for a limited time (default 5 minutes).
     * 
     * @param fileName The name of the file to generate a token for
     * @param requestFingerprint Optional browser fingerprint to bind the token to a specific client
     * @return The generated token
     */
    public String generateFileToken(String fileName, String requestFingerprint) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        long timestamp = Instant.now().getEpochSecond();
        long expiry = timestamp + tokenValiditySeconds;

        // Use fingerprint if provided, or an empty string if not
        String fingerprint = requestFingerprint != null ? requestFingerprint : "";

        // Create a token with: filename + expiry + fingerprint + secret
        String tokenData = fileName + "|" + expiry + "|" + fingerprint + "|" + tokenSecret;

        try {
            // Create SHA-256 hash of the token data
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenData.getBytes(StandardCharsets.UTF_8));

            // Convert hash to base64url (URL-safe)
            String hashBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Final token format: expiry|fingerprint|hash
            return expiry + "|" + Base64.getUrlEncoder().withoutPadding().encodeToString(fingerprint.getBytes()) + "|" + hashBase64;
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating file token: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Overloaded method for backward compatibility
     */
    public String generateFileToken(String fileName) {
        return generateFileToken(fileName, null);
    }

    /**
     * Validates a file access token.
     * 
     * @param fileName The name of the file being accessed
     * @param token    The token to validate
     * @param currentFingerprint Optional browser fingerprint to validate against token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateFileToken(String fileName, String token, String currentFingerprint) {
        if (fileName == null || fileName.isEmpty() || token == null || token.isEmpty()) {
            return false;
        }

        try {
            // Split the token parts: expiry|fingerprint|hash
            String[] parts = token.split("\\|", 3);
            if (parts.length != 3) {
                log.debug("Invalid token format for file: {}", fileName);
                return false;
            }

            // Extract token components
            long expiry = Long.parseLong(parts[0]);
            String tokenFingerprint = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String providedHash = parts[2];

            // Check if token has expired
            long currentTime = Instant.now().getEpochSecond();
            if (currentTime > expiry) {
                log.debug("Token expired for file: {}", fileName);
                return false;
            }

            // If fingerprinting is enabled (token has fingerprint and current fingerprint provided)
            if (!tokenFingerprint.isEmpty() && currentFingerprint != null && !currentFingerprint.isEmpty()) {
                // Token must match the current fingerprint 
                if (!tokenFingerprint.equals(currentFingerprint)) {
                    log.debug("Token fingerprint mismatch for file: {}. Expected: {}, Got: {}", 
                              fileName, tokenFingerprint, currentFingerprint);
                    return false;
                }
            }

            // Recreate the token data to validate
            String tokenData = fileName + "|" + expiry + "|" + tokenFingerprint + "|" + tokenSecret;

            // Compute hash of the token data
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(tokenData.getBytes(StandardCharsets.UTF_8));
            String expectedHash = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Compare expected hash with provided hash
            return expectedHash.equals(providedHash);
        } catch (Exception e) {
            log.error("Error validating file token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Overloaded method for backward compatibility
     */
    public boolean validateFileToken(String fileName, String token) {
        return validateFileToken(fileName, token, null);
    }
}