package com.bpd.prayertime.content.application;

import com.bpd.prayertime.content.domain.Content;
import com.bpd.prayertime.content.domain.ContentRepository;
import com.bpd.prayertime.content.domain.ResourceId;
import com.bpd.prayertime.content.domain.ResourceRepository;

import java.util.List;

public class CreateContent {

    private final ContentRepository contentRepository;
    private final ResourceRepository resourceRepository;

    public CreateContent(ContentRepository contentRepository, ResourceRepository resourceRepository) {
        this.contentRepository = contentRepository;
        this.resourceRepository = resourceRepository;
    }

    public ContentDto execute(CreateContentCommand createContentCommand) {
        for (NewItemCommand newItemCommand : createContentCommand.resources()) {
            boolean exist = resourceRepository.exist(new ResourceId(newItemCommand.resourceId()));
            if (!exist) {
                throw new IllegalArgumentException("Resource not found for " + newItemCommand.resourceId());
            }
        }

        Content.Time time = getTime(createContentCommand);

        Content newContent = Content.createNew(createContentCommand.title(), time);
        createContentCommand.resources()
                .forEach(it -> {
                    ResourceId resourceId = new ResourceId(it.resourceId());
                    newContent.addItem(resourceId, it.sequence());
                });

        Content content = contentRepository.save(newContent);

        return getContentDto(content);
    }

    private static Content.Time getTime(CreateContentCommand createContentCommand) {
        TimeDto timeDto = createContentCommand.time();
        if (timeDto instanceof TimeDto.EveryTimeDto) {
            return new Content.EveryTime();
        }
        if (timeDto instanceof TimeDto.EveryDayOfWeekDto dto) {
            return new Content.EveryDayOfWeek(dto.getDay());
        }
        if (timeDto instanceof TimeDto.EveryDayOfWeekAtTimeDto dto) {
            return new Content.EveryDayOfWeekAtTime(dto.getDay(), dto.getStartTime(), dto.getEndTime());
        }
        throw new IllegalArgumentException("Invalid content");
    }

    private static ContentDto getContentDto(Content content) {
        TimeDto timeDto = getTimeDto(content);
        List<ContentDto.ItemDto> itemDtos = content.items().stream().map(it -> new ContentDto.ItemDto(it.id().id(), it.sequence(), it.resourceId().id()))
                .toList();
        return new ContentDto(content.id().id(), content.title(), timeDto, itemDtos);
    }

    private static TimeDto getTimeDto(Content content) {
        Content.Time time = content.time();
        if (time instanceof Content.EveryTime) {
            return new TimeDto.EveryTimeDto();
        }
        if (time instanceof Content.EveryDayOfWeek domain) {
            return new TimeDto.EveryDayOfWeekDto(domain.dayOfWeek());
        }
        if (time instanceof Content.EveryDayOfWeekAtTime domain) {
            return new TimeDto.EveryDayOfWeekAtTimeDto(domain.dayOfWeek(), domain.startTime(), domain.endTime());
        }
        throw  new IllegalArgumentException("Invalid content");
    }
}
