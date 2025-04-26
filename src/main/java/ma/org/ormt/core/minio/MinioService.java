package ma.org.ormt.core.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.io.InputStream;
import java.util.List;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    public void init() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());

        return fileName;
    }

    /**
     * Convert a file name to a full URL that can be used to access the file.
     * This is useful for frontend applications like Angular.
     * 
     * @param fileName The name of the file stored in MinIO
     * @return The full URL to access the file
     */
    public String getFullUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return minioEndpoint + "/" + bucketName + "/" + fileName;
    }

    public byte[] getFile(String fileName) throws Exception {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());

        return stream.readAllBytes();
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
    }

    /**
     * Sets the bucket policy to allow access only from specific origins (e.g., your
     * frontend applications).
     * This restricts public access while still allowing your frontend to retrieve
     * files.
     *
     * @throws Exception if setting the bucket policy fails
     */
    public void setSecureBucketPolicy() throws Exception {
        // Split the allowed origins if multiple are provided (comma-separated)
        String[] origins = allowedOrigins.split(",");

        // Create a policy that allows access only from specified origins
        StringBuilder conditionsJson = new StringBuilder();
        for (int i = 0; i < origins.length; i++) {
            if (i > 0)
                conditionsJson.append(", ");
            conditionsJson.append("\"").append(origins[i].trim()).append("\"");
        }

        String securePolicy = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\"AWS\": [\"*\"]},\n" +
                "            \"Action\": [\"s3:GetObject\"],\n" +
                "            \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"],\n" +
                "            \"Condition\": {\n" +
                "                \"StringLike\": {\n" +
                "                    \"aws:Referer\": [" + conditionsJson.toString() + "]\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Apply the policy to the bucket
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(securePolicy)
                        .build());
    }

    /**
     * Sets the bucket policy to read-only, making all objects in the bucket
     * publicly accessible for read operations.
     * This is useful for making files available to the frontend without
     * authentication.
     *
     * @throws Exception if setting the bucket policy fails
     * @deprecated Use setSecureBucketPolicy() for more secure access control
     */
    @Deprecated
    public void setBucketReadOnlyPolicy() throws Exception {
        // Create a policy JSON string that grants read-only access to all objects in
        // the bucket
        String readOnlyPolicy = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\n" +
                "                \"AWS\": [\"*\"]\n" +
                "            },\n" +
                "            \"Action\": [\"s3:GetObject\"],\n" +
                "            \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // Apply the policy to the bucket
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(readOnlyPolicy)
                        .build());
    }
}
