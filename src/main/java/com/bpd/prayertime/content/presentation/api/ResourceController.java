package com.bpd.prayertime.content.presentation.api;

import com.bpd.prayertime.content.application.CreateResource;
import com.bpd.prayertime.content.domain.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final CreateResource createResource;

    public ResourceController(CreateResource createResource) {
        this.createResource = createResource;
    }

    @PostMapping
    public Resource createContent(@RequestPart MultipartFile file) throws IOException {
        return createResource.execute(file);
    }
}
