package com.bpd.prayertime.prayertime.domain;

import java.time.LocalDate;

public interface OneDayPrayerTimeRepository {
    OneDayPrayerTime getOneDayPrayerTime(Coordinate coordinate, LocalDate date);

    OneDayPrayerTime getOneDayPrayerTime(City city, LocalDate date);
}
