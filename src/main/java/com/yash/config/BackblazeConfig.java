package com.yash.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.time.Duration;

@Configuration
public class BackblazeConfig {

    @Value("${backblaze.accessKeyId}")
    private String accessKeyId;

    @Value("${backblaze.secretAccessKey}")
    private String secretAccessKey;

    @Value("${backblaze.endpoint}")
    private String endpoint;

    @Value("${backblaze.region:us-west-001}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(60))
                .socketTimeout(Duration.ofSeconds(60))
                .maxConnections(100)
                .tcpKeepAlive(true);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create("https://" + endpoint))
                .httpClientBuilder(httpClientBuilder)
                .forcePathStyle(true)
                .build();
    }
}