package ma.org.ormt.seeder.config.minio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(3)
public class MinioInitilizer implements CommandLineRunner {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.initialize}")
    private boolean initializeMinio;

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

            if (!bucketExists) {
                // Create the bucket if it doesn't exist
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Successfully created bucket: {}", bucketName);
            } else {
                log.info("Bucket already exists: {}", bucketName);
            }
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error initializing MinIO bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize MinIO bucket", e);
        }
    }

}
