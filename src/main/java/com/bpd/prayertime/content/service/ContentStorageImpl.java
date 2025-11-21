package com.bpd.prayertime.content.service;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Component
public class ContentStorageImpl implements ContentStorage {

    private static final String CONTENT_BUCKET = "content";
    private final S3Client s3Client;

    public ContentStorageImpl(S3Client s3Client) {
        this.s3Client = s3Client;
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
