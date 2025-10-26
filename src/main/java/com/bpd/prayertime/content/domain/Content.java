package com.bpd.prayertime.content.domain;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record Content(ContentId id, String title, Time time) {

    public Content {
        if (title == null) {
            throw new IllegalArgumentException("Title can't be null");
        }

        if (time == null) {
            throw new IllegalArgumentException("Time can't be null");
        }
    }


    public record ContentId(Long id) {
    }

    public interface Time {
        Range getRange();

        boolean canBeUsedOn(LocalDateTime localDateTime);
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

    public static class EveryTime implements Time {

        @Override
        public Range getRange() {
            return new Range(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59));
        }

        @Override
        public boolean canBeUsedOn(LocalDateTime localDateTime) {
            return true;
        }
    }

    public static class EveryDayOfWeek implements Time {

        private final DayOfWeek dayOfWeek;

        public EveryDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        @Override
        public Range getRange() {
            return new Range(LocalTime.of(0, 0, 0), LocalTime.of(23, 59, 59));
        }

        @Override
        public boolean canBeUsedOn(LocalDateTime localDateTime) {
            return localDateTime.getDayOfWeek().equals(dayOfWeek);
        }
    }

    public static class EveryDayOfWeekAtTime implements Time {

        private final DayOfWeek dayOfWeek;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public EveryDayOfWeekAtTime(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
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

            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Range getRange() {
            return new Range(startTime, endTime);
        }

        @Override
        public boolean canBeUsedOn(LocalDateTime localDateTime) {
            return localDateTime.getDayOfWeek().equals(dayOfWeek);
        }
    }
}
