package com.bpd.prayertime.prayertime.service;

import java.time.LocalTime;

public record PrayerTimeResponseDto(
        LocalTime fajr,
        LocalTime dhuhr,
        LocalTime asr,
        LocalTime maghrib,
        LocalTime isha,
        String hijriDate
) {
}
