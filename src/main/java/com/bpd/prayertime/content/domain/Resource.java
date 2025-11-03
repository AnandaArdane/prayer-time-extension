package com.bpd.prayertime.content.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Resource(ResourceId id, File file) {
    public Resource {
        if (id == null) {
            throw new IllegalArgumentException("ID can't be null");
        }

        if (file == null) {
            throw new IllegalArgumentException("File can't be null");
        }

        if (!isValidByName(file)) {
            throw new RuntimeException("File is not valid");
        }

        if (!isValidByMimeType(file)) {
            throw new RuntimeException("File is not valid");
        }
    }

    private boolean isValidByName(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png");
    }

    private boolean isValidByMimeType(File file) {
        Path path = file.toPath();
        try {
            String mimeType = Files.probeContentType(path);
            if (mimeType == null) return false;
            return mimeType.equals("image/jpeg") || mimeType.equals("image/png");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Resource createNew(File file) {
        return new Resource(ResourceId.setAsNew(), file);
    }
}
