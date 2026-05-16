package com.bpd.prayertime.prayertime.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PrayerTimeService {

    private final AladhanService aladhanService;
    private final HijriDateCalculator hijriDateCalculator;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public PrayerTimeService(AladhanService aladhanService, HijriDateCalculator hijriDateCalculator) {
        this.aladhanService = aladhanService;
        this.hijriDateCalculator = hijriDateCalculator;
    }

    public PrayerTimeResponseDto getByCity(String cityName, LocalDate date) {
        Map<String, String> timings = aladhanService.getTimingsByCity(cityName, date);
        return mapToDto(timings, date);
    }

    public PrayerTimeResponseDto getByCoordinate(Double latitude, Double longitude, LocalDate date) {
        Map<String, String> timings = aladhanService.getTimingsByCoordinate(latitude, longitude, date);
        return mapToDto(timings, date);
    }

    private PrayerTimeResponseDto mapToDto(Map<String, String> timings, LocalDate date) {
        LocalTime maghrib = LocalTime.parse(timings.get("Maghrib"), TIME_FORMATTER);
        String hijriDate = hijriDateCalculator.calculateHijriDate(date, maghrib);

        return new PrayerTimeResponseDto(
                LocalTime.parse(timings.get("Fajr"), TIME_FORMATTER),
                LocalTime.parse(timings.get("Dhuhr"), TIME_FORMATTER),
                LocalTime.parse(timings.get("Asr"), TIME_FORMATTER),
                maghrib,
                LocalTime.parse(timings.get("Isha"), TIME_FORMATTER),
                hijriDate
        );
    }
}
