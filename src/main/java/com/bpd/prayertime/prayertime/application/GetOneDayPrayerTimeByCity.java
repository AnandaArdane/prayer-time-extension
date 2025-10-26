package com.bpd.prayertime.prayertime.application;

import com.bpd.prayertime.prayertime.domain.City;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;

import java.time.LocalDate;

public class GetOneDayPrayerTimeByCity {

    private final OneDayPrayerTimeRepository oneDayPrayerTimeRepository;

    public GetOneDayPrayerTimeByCity(OneDayPrayerTimeRepository oneDayPrayerTimeRepository) {
        this.oneDayPrayerTimeRepository = oneDayPrayerTimeRepository;
    }

    public OneDayPrayerTimeDto execute(String cityName, LocalDate date) {
        OneDayPrayerTime oneDayPrayerTime = oneDayPrayerTimeRepository.getOneDayPrayerTime(new City(cityName), date);
        return new OneDayPrayerTimeDto(
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.SHUBUH),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.DHUHR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ASR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.MAGHRIB),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ISHA)
        );
    }
}
