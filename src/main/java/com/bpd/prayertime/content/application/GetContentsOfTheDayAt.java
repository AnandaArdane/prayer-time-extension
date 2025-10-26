package com.bpd.prayertime.content.application;

import com.bpd.prayertime.content.domain.Content;
import com.bpd.prayertime.content.domain.ContentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GetContentsOfTheDayAt {

    private final ContentRepository contentRepository;

    public GetContentsOfTheDayAt(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public List<TimelineDto> execute(LocalDate localDate) {
        List<Content> contents = contentRepository.getContentAt(localDate);
        Set<Content.Range> ranges = contents.stream()
                .map(content -> content.time().getRange())
                .collect(Collectors.toSet());

        List<LocalTime> timelines = ranges.stream()
                .map(range -> Set.of(range.start(), range.end()))
                .flatMap(Collection::stream)
                .sorted()
                .distinct()
                .toList();

        List<Content.Range> timelineRanges = new ArrayList<>();
        for (int i = 1; i < timelines.size(); i++) {
            Content.Range range = new Content.Range(timelines.get(i), timelines.get(i - 1));
            timelineRanges.add(range);
        }

        return timelineRanges.stream()
                .map(timelineRange -> getTimelineDto(localDate, timelineRange, contents))
                .toList();
    }

    private static TimelineDto getTimelineDto(LocalDate localDate, Content.Range timelineRange, List<Content> contents) {
        LocalDateTime startOfTimeline = LocalDateTime.of(localDate, timelineRange.start());

        List<TimelineDto.ContentDto> contentDto = contents.stream()
                .filter(it -> it.time().canBeUsedOn(startOfTimeline))
                .map(it -> new TimelineDto.ContentDto(it.id().id(), it.title()))
                .toList();

        TimelineDto.RangeDto rangeDto = new TimelineDto.RangeDto(timelineRange.start(), timelineRange.end());
        return new TimelineDto(rangeDto, contentDto);
    }
}
