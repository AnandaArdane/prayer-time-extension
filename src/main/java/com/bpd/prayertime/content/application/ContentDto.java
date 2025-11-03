package com.bpd.prayertime.content.application;

import java.util.List;

public record ContentDto(Long id, String title, TimeDto time, List<ItemDto> items) {
    public record ItemDto(Long id, Integer sequence, Long resourceId) {}
}
