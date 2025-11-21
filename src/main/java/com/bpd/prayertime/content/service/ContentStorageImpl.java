package com.bpd.prayertime.content.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ContentStorageImpl implements ContentStorage {

    private static final String CONTENT_BUCKET = "content";
    private final S3Client s3Client;

    public ContentStorageImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void initBucket() {
        if (!bucketExists()) {
            createBucket();
        }
    }

    private boolean bucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(CONTENT_BUCKET)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private void createBucket() {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(CONTENT_BUCKET)
                .build());
        log.info("Bucket created: " + CONTENT_BUCKET);
    }

    @Override
    public void store(Long id, InputStream inputStream, String fileName, long fileSize) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(CONTENT_BUCKET)
                        .key(id.toString())
                        .metadata(
                                Map.of(
                                        "filename", Objects.requireNonNull(fileName)
                                )
                        )
                        .build(),
                RequestBody.fromInputStream(inputStream, fileSize)
        );
    }

    @Override
    public void deleteById(Long id) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(CONTENT_BUCKET)
                .key(id.toString())
                .build()
        );
    }
}
