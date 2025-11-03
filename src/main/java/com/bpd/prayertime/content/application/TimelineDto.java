package com.bpd.prayertime.content.application;

import java.time.LocalTime;
import java.util.List;

public record TimelineDto(RangeDto range, List<ContentDto> contents) {

    public record RangeDto(LocalTime start, LocalTime end) {}

    public record ContentDto(Long id, String title, List<ContentItemDto> items) {}

    public record ContentItemDto(Long id, Integer sequence, Long resourceId) {}
}
