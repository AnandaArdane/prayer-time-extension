package com.bpd.prayertime.prayertime.presentation.api;


import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCity;
import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCoordinate;
import com.bpd.prayertime.prayertime.application.OneDayPrayerTimeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/prayers")
public class PrayerTimeController {

    private final GetOneDayPrayerTimeByCity getOneDayPrayerTimeByCity;
    private final GetOneDayPrayerTimeByCoordinate getOneDayPrayerTimeByCoordinate;

    public PrayerTimeController(GetOneDayPrayerTimeByCity getOneDayPrayerTimeByCity, GetOneDayPrayerTimeByCoordinate getOneDayPrayerTimeByCoordinate) {
        this.getOneDayPrayerTimeByCity = getOneDayPrayerTimeByCity;
        this.getOneDayPrayerTimeByCoordinate = getOneDayPrayerTimeByCoordinate;
    }

    @GetMapping("/by-city")
    public OneDayPrayerTimeDto getByCity(@RequestParam String name, @RequestParam LocalDate date) {
        return getOneDayPrayerTimeByCity.execute(name, date);
    }

    @GetMapping("/by-coordinate")
    public OneDayPrayerTimeDto getByCoordinate(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam LocalDate date) {
        return getOneDayPrayerTimeByCoordinate.execute(latitude, longitude, date);
    }
}
