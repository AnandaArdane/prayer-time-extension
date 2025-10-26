package com.bpd.prayertime.prayertime.application;

import java.time.LocalTime;

public record OneDayPrayerTimeDto(
        LocalTime fajr,
        LocalTime dhuhr,
        LocalTime asr,
        LocalTime maghrib,
        LocalTime isha
) {
}
