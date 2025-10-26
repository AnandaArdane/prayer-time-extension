package com.bpd.prayertime.prayertime.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record OneDayPrayerTime(List<PrayerTime> prayerTimes) {
    public OneDayPrayerTime {
        if (prayerTimes.size() != 5) {
            throw new IllegalArgumentException("Prayer time should 5 time a day");
        }

        Set<Time> times = prayerTimes.stream().map(it -> it.prayerTime).collect(Collectors.toSet());
        if (times.size() != 5) {
            throw new IllegalArgumentException("Times is not complete");
        }
    }

    public LocalTime getTimeFor(Time time) {
        return prayerTimes().stream()
                .filter(it -> it.prayerTime.equals(time))
                .map(it -> it.time)
                .findFirst()
                .orElseThrow();
    }

    public record PrayerTime(Time prayerTime, LocalTime time) {
        public PrayerTime {
            if (prayerTime == null) {
                throw new IllegalArgumentException("Prayer should not be null");
            }

            if (time == null) {
                throw new IllegalArgumentException("Time should not be null");
            }
        }
    }

    public enum Time {
        SHUBUH, DHUHR, ASR, MAGHRIB, ISHA
    }
}

