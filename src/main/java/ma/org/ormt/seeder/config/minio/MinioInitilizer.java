package ma.org.ormt.seeder.config.minio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.minio.MinioService;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(3)
public class MinioInitilizer implements CommandLineRunner {

    private final MinioClient minioClient;
    private final MinioService minioService;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${starter.minio.seed}")
    private boolean initializeMinio;

    @Value("${starter.minio.reset}")
    private boolean resetBucket;

    @Override
    public void run(String... args) throws Exception {

        if (!initializeMinio) {
            log.info("### MINIO: Skipping minio initialization");
            return;
        }
        try {
            // Check if the bucket already exists
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());

            if (bucketExists) {
                if (resetBucket) {
                    // Remove all objects in the bucket before deleting it
                    log.info("### MINIO: Removing all objects from bucket: {}", bucketName);
                    removeAllObjectsInBucket();

                    // Now remove the empty bucket
                    log.info("### MINIO: Resetting bucket: {}", bucketName);
                    minioClient.removeBucket(
                            RemoveBucketArgs.builder().bucket(bucketName).build());
                    log.info("### MINIO: Successfully removed bucket: {}", bucketName);
                    bucketExists = false;
                } else {
                    log.info("Bucket already exists: {}", bucketName);
                }
            }

            if (!bucketExists) {
                // Create the bucket if it doesn't exist or was removed
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Successfully created bucket: {}", bucketName);
            }

            // Apply secure bucket policy for all environments (dev and prod)
            minioService.setSecureBucketPolicy();
            log.info("### MINIO: Applied secure bucket policy restricting access to allowed origins");

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

    /**
     * Remove all objects from the bucket
     */
    private void removeAllObjectsInBucket() throws MinioException, InvalidKeyException,
            NoSuchAlgorithmException, IOException {

        // List all objects in the bucket
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());

        List<String> objectsToDelete = new ArrayList<>();

        // Collect all object names
        for (Result<Item> result : results) {
            Item item = result.get();
            objectsToDelete.add(item.objectName());
        }

        // Delete each object
        for (String objectName : objectsToDelete) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            log.debug("Removed object: {}", objectName);
        }
        log.info("### MINIO: Removed {} objects from bucket: {}", objectsToDelete.size(), bucketName);
    }
}
