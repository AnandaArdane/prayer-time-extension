package com.bpd.prayertime.prayertime.application;

import com.bpd.prayertime.prayertime.domain.Coordinate;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;

import java.time.LocalDate;

public class GetOneDayPrayerTimeByCoordinate {

    private final OneDayPrayerTimeRepository oneDayPrayerTimeRepository;

    public GetOneDayPrayerTimeByCoordinate(OneDayPrayerTimeRepository oneDayPrayerTimeRepository) {
        this.oneDayPrayerTimeRepository = oneDayPrayerTimeRepository;
    }

    public OneDayPrayerTimeDto execute(Double latitude, Double longitude, LocalDate date) {
        Coordinate coordinate = new Coordinate(new Coordinate.Latitude(latitude), new Coordinate.Longitude(longitude));
        OneDayPrayerTime oneDayPrayerTime = oneDayPrayerTimeRepository.getOneDayPrayerTime(coordinate, date);
        return new OneDayPrayerTimeDto(
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.SHUBUH),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.DHUHR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ASR),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.MAGHRIB),
                oneDayPrayerTime.getTimeFor(OneDayPrayerTime.Time.ISHA)
        );
    }
}
