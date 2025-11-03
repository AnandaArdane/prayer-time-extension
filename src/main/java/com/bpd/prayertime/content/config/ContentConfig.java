package com.bpd.prayertime.content.config;

import com.bpd.prayertime.content.application.CreateContent;
import com.bpd.prayertime.content.application.CreateResource;
import com.bpd.prayertime.content.application.GetContentsOfTheDayAt;
import com.bpd.prayertime.content.domain.ContentRepository;
import com.bpd.prayertime.content.domain.ResourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentConfig {

    @Bean
    public CreateContent createContent(ContentRepository contentRepository, ResourceRepository resourceRepository) {
        return new CreateContent(contentRepository, resourceRepository);
    }

    @Bean
    public CreateResource createResource(ResourceRepository resourceRepository) {
        return new CreateResource(resourceRepository);
    }

    @Bean
    public GetContentsOfTheDayAt getContentsOfTheDayAt(ContentRepository contentRepository) {
        return new GetContentsOfTheDayAt(contentRepository);
    }
}
