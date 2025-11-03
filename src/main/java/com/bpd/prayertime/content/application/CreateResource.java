package com.bpd.prayertime.content.application;

import com.bpd.prayertime.content.domain.Resource;
import com.bpd.prayertime.content.domain.ResourceRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateResource {
    private final ResourceRepository resourceRepository;

    public CreateResource(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource execute(MultipartFile multipartFile) {
        File savedFile = getFile(multipartFile);
        Resource newResource = Resource.createNew(savedFile);
        return resourceRepository.save(newResource);
    }

    private static File getFile(MultipartFile multipartFile) {
        Path destinationPath = Paths.get("/prayer-time/resource", multipartFile.getOriginalFilename());

        try {
            multipartFile.transferTo(destinationPath);
            return destinationPath.toFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid to save file");
        }
    }
}
