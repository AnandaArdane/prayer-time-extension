package com.bpd.prayertime.prayertime.service;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class HijriDateCalculator {

    private final Clock clock;

    public HijriDateCalculator(Clock clock) {
        this.clock = clock;
    }

    public String calculateHijriDate(LocalDate date, LocalTime maghribTime) {
        LocalDate hijriBaseDate = date;
        LocalDate today = LocalDate.now(clock);

        if (date.equals(today)) {
            LocalTime now = LocalTime.now(clock);
            if (now.isAfter(maghribTime)) {
                hijriBaseDate = date.plusDays(1);
            }
        }

        HijrahDate hijrahDate = HijrahDate.from(hijriBaseDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy G", new Locale("id", "ID"));
        return formatter.format(hijrahDate).replace("Minggu", "Ahad");
    }
}
