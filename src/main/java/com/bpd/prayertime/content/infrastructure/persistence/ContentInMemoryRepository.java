package com.bpd.prayertime.content.infrastructure.persistence;

import com.bpd.prayertime.content.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ContentInMemoryRepository implements ContentRepository {

    private final List<Content> contents = new ArrayList<>();
    private long contentCounter = 0;
    private long itemCounter = 0;

    @Override
    public List<Content> getContentAt(LocalDate localDate) {
        return contents.stream().filter(it -> {
            Content.Time time = it.time();
            if (time instanceof Content.EveryTime) {
                return true;
            }
            if (time instanceof Content.EveryDayOfWeek domain) {
                return Objects.equals(domain.dayOfWeek(), localDate.getDayOfWeek());
            }
            if (time instanceof Content.EveryDayOfWeekAtTime domain) {
                return Objects.equals(domain.dayOfWeek(), localDate.getDayOfWeek());
            }
            return false;
        }).toList();
    }

    @Override
    public Content save(Content content) {
        boolean isNew = content.id().isNew();
        if (isNew) {
            contentCounter++;
            ContentId contentId = new ContentId(contentCounter);

            List<Item> newItems = content.items().stream().map(it -> {
                itemCounter++;
                return new Item(new ItemId(itemCounter), it.sequence(), contentId, it.resourceId());
            }).toList();


            Content newContent = new Content(contentId, content.title(), content.time(), newItems);
            contents.add(newContent);
            return newContent;
        }

        Content matchedContent = contents.stream().filter(it -> Objects.equals(it.id(), content.id()))
                .findFirst()
                .orElseThrow();
        contents.remove(matchedContent);
        contents.add(content);
        return content;
    }
}
