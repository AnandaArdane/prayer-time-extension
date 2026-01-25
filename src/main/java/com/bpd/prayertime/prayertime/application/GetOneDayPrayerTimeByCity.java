package com.bpd.prayertime.prayertime.application;

import com.bpd.prayertime.prayertime.domain.City;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GetOneDayPrayerTimeByCity {

    private final OneDayPrayerTimeRepository oneDayPrayerTimeRepository;

    public GetOneDayPrayerTimeByCity(OneDayPrayerTimeRepository oneDayPrayerTimeRepository) {
        this.oneDayPrayerTimeRepository = oneDayPrayerTimeRepository;
    }

    public OneDayPrayerTimeDto execute(String cityName, LocalDate date) {
        OneDayPrayerTime oneDayPrayerTime = oneDayPrayerTimeRepository.getOneDayPrayerTime(new City(cityName), date);
        HijrahDate hijrahDate = HijrahDate.from(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy G", new Locale("id", "ID"));
        String formattedHijriDate = formatter.format(hijrahDate);

        return new OneDayPrayerTimeDto(
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.SHUBUH),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.DHUHR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ASR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.MAGHRIB),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ISHA),
                formattedHijriDate);
    }
}
