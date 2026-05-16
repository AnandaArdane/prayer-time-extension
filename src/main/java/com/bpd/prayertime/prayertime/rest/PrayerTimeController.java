package com.bpd.prayertime.prayertime.rest;

import com.bpd.prayertime.prayertime.service.PrayerTimeResponseDto;
import com.bpd.prayertime.prayertime.service.PrayerTimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/prayers")
public class PrayerTimeController {

    private final PrayerTimeService prayerTimeService;

    public PrayerTimeController(PrayerTimeService prayerTimeService) {
        this.prayerTimeService = prayerTimeService;
    }

    @GetMapping("/by-city")
    public PrayerTimeResponseDto getByCity(@RequestParam String name, @RequestParam LocalDate date) {
        return prayerTimeService.getByCity(name, date);
    }

    @GetMapping("/by-coordinate")
    public PrayerTimeResponseDto getByCoordinate(@RequestParam Double latitude, @RequestParam Double longitude, @RequestParam LocalDate date) {
        return prayerTimeService.getByCoordinate(latitude, longitude, date);
    }
}
