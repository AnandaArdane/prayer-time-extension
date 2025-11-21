package com.bpd.prayertime.content.service.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ContentFileValidation {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp"
    );


    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File should not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Not allowed file detected");
        }
    }
}
