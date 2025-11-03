package com.bpd.prayertime.content.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record Content(ContentId id, String title, Time time, List<Item> items) {

    public Content {
        if (title == null) {
            throw new IllegalArgumentException("Title can't be null");
        }

        if (time == null) {
            throw new IllegalArgumentException("Time can't be null");
        }
    }

    public interface Time {
        Range getRange();

        boolean canBeUsedOn(Range range);
    }

    public record Range(LocalTime start, LocalTime end) {
        public Range {
            if (start == null) {
                throw new IllegalArgumentException("Start can't be null");
            }
            if (end == null) {
                throw new IllegalArgumentException("End can't be null");
            }
        }
    }

    public static Content createNew(String title, Time time) {
        return new Content(ContentId.setAsNew(), title, time, new ArrayList<>());
    }

    public void addItem(ResourceId resourceId, Integer sequence) {
        Item newItem = Item.createNew(id, resourceId, sequence);
        this.items.add(newItem);
    }

    public record EveryTime() implements Time {

        @Override
        public Range getRange() {
            return new Range(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59));
        }

        @Override
        public boolean canBeUsedOn(Range range) {
            return true;
        }
    }

    public record EveryDayOfWeek(DayOfWeek dayOfWeek) implements Time {

        @Override
        public Range getRange() {
            return new Range(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59));
        }

        @Override
        public boolean canBeUsedOn(Range range) {
            return true;
        }
    }

    public record EveryDayOfWeekAtTime(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) implements Time {

        public EveryDayOfWeekAtTime {
            if (dayOfWeek == null) {
                throw new IllegalArgumentException("Day of week can't be null");
            }

            if (startTime == null) {
                throw new IllegalArgumentException("Start time of week can't be null");
            }

            if (endTime == null) {
                throw new IllegalArgumentException("End time of week can't be null");
            }

            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException("Start time should not after end time");
            }

        }

        @Override
        public Range getRange() {
            return new Range(startTime, endTime);
        }

        @Override
        public boolean canBeUsedOn(Range range) {
            LocalTime startRange = range.start;
            LocalTime endRange = range.end;

            boolean startIsOkay = startRange.isAfter(startTime) || startRange.equals(startTime);
            boolean endIsOkay = endRange.isBefore(endTime) || endRange.equals(endTime);
            return startIsOkay && endIsOkay;
        }
    }
}
