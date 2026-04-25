package com.bpd.prayertime.studysession.service;

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
public class StudySessionStorageImpl implements StudySessionStorage {

    private static final String STUDY_SESSION_BUCKET = "study-session";
    private final S3Client s3Client;

    public StudySessionStorageImpl(S3Client s3Client) {
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
                    .bucket(STUDY_SESSION_BUCKET)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private void createBucket() {
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(STUDY_SESSION_BUCKET)
                .build());
        log.info("Bucket created: " + STUDY_SESSION_BUCKET);
    }

    @Override
    public void store(Long id, InputStream inputStream, String fileName, long fileSize) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(STUDY_SESSION_BUCKET)
                        .key(id.toString())
                        .metadata(
                                Map.of(
                                        "filename", Objects.requireNonNull(fileName)))
                        .build(),
                RequestBody.fromInputStream(inputStream, fileSize));
    }

    @Override
    public void deleteById(Long id) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(STUDY_SESSION_BUCKET)
                .key(id.toString())
                .build());
    }

    @Override
    public InputStream retrieve(Long id) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(STUDY_SESSION_BUCKET)
                .key(id.toString())
                .build());
    }
}
