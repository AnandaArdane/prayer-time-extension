package com.bpd.prayertime.prayertime.application;

import com.bpd.prayertime.prayertime.domain.City;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public class GetOneDayPrayerTimeByCity {

    private final OneDayPrayerTimeRepository oneDayPrayerTimeRepository;
    private final HijriDateCalculator hijriDateCalculator;

    public GetOneDayPrayerTimeByCity(OneDayPrayerTimeRepository oneDayPrayerTimeRepository,
            HijriDateCalculator hijriDateCalculator) {
        this.oneDayPrayerTimeRepository = oneDayPrayerTimeRepository;
        this.hijriDateCalculator = hijriDateCalculator;
    }

    public OneDayPrayerTimeDto execute(String cityName, LocalDate date) {
        OneDayPrayerTime oneDayPrayerTime = oneDayPrayerTimeRepository.getOneDayPrayerTime(new City(cityName), date);

        LocalTime maghrib = oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.MAGHRIB);
        String formattedHijriDate = hijriDateCalculator.calculateHijriDate(date, maghrib);

        return new OneDayPrayerTimeDto(
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.SHUBUH),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.DHUHR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ASR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.MAGHRIB),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ISHA),
                formattedHijriDate);
    }
}
