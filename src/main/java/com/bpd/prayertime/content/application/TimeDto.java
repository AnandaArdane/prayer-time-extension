package com.bpd.prayertime.content.application;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.DayOfWeek;
import java.time.LocalTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimeDto.EveryTimeDto.class, name = "everyTime"),
        @JsonSubTypes.Type(value = TimeDto.EveryDayOfWeekDto.class, name = "everyDayOfWeek"),
        @JsonSubTypes.Type(value = TimeDto.EveryDayOfWeekAtTimeDto.class, name = "everyDayOfWeekAtTime")
})
public abstract class TimeDto {

    public static class EveryTimeDto extends TimeDto {

        public EveryTimeDto() {}
    }

    public static class EveryDayOfWeekDto extends TimeDto {
        private final DayOfWeek day;

        public EveryDayOfWeekDto(DayOfWeek day) {
            this.day = day;
        }

        public DayOfWeek getDay() {
            return day;
        }
    }

    public static class EveryDayOfWeekAtTimeDto extends TimeDto {
        private final DayOfWeek day;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public EveryDayOfWeekAtTimeDto(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public DayOfWeek getDay() {
            return day;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }
    }
}
