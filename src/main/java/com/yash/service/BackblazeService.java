package com.yash.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BackblazeService {

    @Value("${backblaze.accessKeyId}")
    private String accessKeyId;

    @Value("${backblaze.secretAccessKey}")
    private String secretAccessKey;

    @Value("${backblaze.bucketName}")
    private String bucketName;

    @Value("${backblaze.endpoint}")
    private String endpoint;

    @Value("${backblaze.region:us-west-001}")
    private String region;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        try {
            // Debug: Print what values are loaded
            System.out.println("=================================================");
            System.out.println("📋 BACKBLAZE CONFIGURATION LOADED:");
            System.out.println("   accessKeyId: " + (accessKeyId != null ? accessKeyId.substring(0, Math.min(10, accessKeyId.length())) + "..." : "NULL"));
            System.out.println("   secretAccessKey: " + (secretAccessKey != null ? "***" + secretAccessKey.substring(Math.max(0, secretAccessKey.length() - 4)) : "NULL"));
            System.out.println("   bucketName: " + bucketName);
            System.out.println("   endpoint: " + endpoint);
            System.out.println("   region: " + region);
            System.out.println("=================================================");

            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalStateException("Endpoint is null or empty! Check your application.properties");
            }

            if (accessKeyId == null || secretAccessKey == null) {
                throw new IllegalStateException("Credentials are null! Check your application.properties");
            }

            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

            // Configure HTTP client with extended timeouts
            ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(60))
                    .socketTimeout(Duration.ofSeconds(60))
                    .connectionAcquisitionTimeout(Duration.ofSeconds(60))
                    .maxConnections(100)
                    .tcpKeepAlive(true);

            String fullEndpoint = "https://" + endpoint.trim();
            System.out.println("🔗 Creating S3 Client with endpoint: " + fullEndpoint);

            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .endpointOverride(URI.create(fullEndpoint))
                    .httpClientBuilder(httpClientBuilder)
                    .forcePathStyle(true)
                    .build();

            System.out.println("✅ Backblaze S3 Client (v2) initialized successfully");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("=================================================");
            System.err.println("❌ Failed to initialize Backblaze S3 Client");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("=================================================");
            e.printStackTrace();
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String fileName = generateFileName(file.getOriginalFilename());

            System.out.println("⬆️  Uploading file: " + fileName + " (" + file.getSize() + " bytes)");

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .cacheControl("max-age=31536000")
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            System.out.println("✅ File uploaded successfully: " + fileName);
            System.out.println("📝 ETag: " + response.eTag());

            return getFileUrl(fileName);

        } catch (S3Exception e) {
            System.err.println("❌ S3 Exception: " + e.awsErrorDetails().errorMessage());
            throw new IOException("S3 Error: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Upload failed: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to upload file to Backblaze B2: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            System.out.println("🗑️  Deleting file: " + fileName);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("✅ File deleted successfully: " + fileName);

        } catch (Exception e) {
            System.err.println("❌ Delete failed: " + e.getMessage());
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    public String getFileUrl(String fileName) {
        return String.format("https://%s/%s/%s", endpoint, bucketName, fileName);
    }

    public List<FileInfo> listFiles() {
        try {
            List<FileInfo> fileInfoList = new ArrayList<>();

            System.out.println("📋 Listing files from bucket: " + bucketName);

            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            for (S3Object s3Object : listObjectsResponse.contents()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(s3Object.key());
                fileInfo.setFileSize(s3Object.size());
                fileInfo.setLastModified(java.util.Date.from(s3Object.lastModified()));
                fileInfo.setUrl(getFileUrl(s3Object.key()));
                fileInfo.setStorageClass(s3Object.storageClassAsString());
                fileInfoList.add(fileInfo);
            }

            System.out.println("✅ Found " + fileInfoList.size() + " file(s)");

            return fileInfoList;

        } catch (Exception e) {
            System.err.println("❌ List files failed: " + e.getMessage());
            throw new RuntimeException("Failed to list files: " + e.getMessage(), e);
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    public String testConnection() {
        try {
            System.out.println("=================================================");
            System.out.println("🔍 Testing connection to Backblaze B2...");
            System.out.println("📍 Endpoint: https://" + endpoint);
            System.out.println("🪣 Bucket: " + bucketName);
            System.out.println("🌍 Region: " + region);
            System.out.println("🔑 Access Key ID: " + accessKeyId.substring(0, Math.min(10, accessKeyId.length())) + "...");
            System.out.println("=================================================");

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .maxKeys(1)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            System.out.println("✅ Connection test successful");
            System.out.println("📊 Objects in bucket: " + response.keyCount());
            System.out.println("=================================================");
            return "SUCCESS: Connected successfully. Found " + response.keyCount() + " objects in bucket.";

        } catch (S3Exception e) {
            String errorMsg = String.format(
                    "S3 Error - Status: %d, Code: %s, Message: %s",
                    e.statusCode(),
                    e.awsErrorDetails().errorCode(),
                    e.awsErrorDetails().errorMessage()
            );
            System.err.println("=================================================");
            System.err.println("❌ " + errorMsg);
            System.err.println("   Service: " + e.awsErrorDetails().serviceName());
            System.err.println("   Request ID: " + e.requestId());
            System.err.println("=================================================");
            e.printStackTrace();
            return errorMsg;
        } catch (Exception e) {
            String errorMsg = String.format(
                    "%s: %s (Cause: %s)",
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e.getCause() != null ? e.getCause().getMessage() : "None"
            );
            System.err.println("=================================================");
            System.err.println("❌ " + errorMsg);
            System.err.println("=================================================");
            e.printStackTrace();
            return errorMsg;
        }
    }

    public static class FileInfo {
        private String fileName;
        private Long fileSize;
        private java.util.Date lastModified;
        private String url;
        private String storageClass;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public java.util.Date getLastModified() {
            return lastModified;
        }

        public void setLastModified(java.util.Date lastModified) {
            this.lastModified = lastModified;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStorageClass() {
            return storageClass;
        }

        public void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }
    }


    public String generateSignedUrl(String fileName) {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey
        );

        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of("us-east-005"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(java.net.URI.create("https://s3.us-east-005.backblazeb2.com"))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("my-ecommerce-files")
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7)) // URL valid 7 days
                .getObjectRequest(getObjectRequest)
                .build();

        URL signedUrl = presigner.presignGetObject(presignRequest).url();

        return signedUrl.toString();
    }


}