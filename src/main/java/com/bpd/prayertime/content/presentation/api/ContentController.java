package com.bpd.prayertime.content.presentation.api;

import com.bpd.prayertime.content.application.ContentDto;
import com.bpd.prayertime.content.application.CreateContent;
import com.bpd.prayertime.content.application.CreateContentCommand;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final CreateContent createContent;

    public ContentController(CreateContent createContent) {
        this.createContent = createContent;
    }

    @PostMapping
    public ContentDto createContent(@RequestBody CreateContentCommand createContentCommand) {
        return createContent.execute(createContentCommand);
    }
}
