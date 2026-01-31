package com.bpd.prayertime.prayertime.application;

import com.bpd.prayertime.prayertime.domain.Coordinate;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetOneDayPrayerTimeByCoordinateTest {

    @Mock
    private OneDayPrayerTimeRepository repository;

    @Mock
    private HijriDateCalculator hijriDateCalculator;

    @InjectMocks
    private GetOneDayPrayerTimeByCoordinate service;

    @Test
    void execute_shouldIncludeHijriDateFromCalculator() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 1, 25);
        LocalTime maghribTime = LocalTime.of(18, 15);
        OneDayPrayerTime mockPrayerTime = new OneDayPrayerTime(List.of(
                new OneDayPrayerTime.PrayerTime(OneDayPrayerTime.Time.SHUBUH, LocalTime.of(4, 30)),
                new OneDayPrayerTime.PrayerTime(OneDayPrayerTime.Time.DHUHR, LocalTime.of(12, 0)),
                new OneDayPrayerTime.PrayerTime(OneDayPrayerTime.Time.ASR, LocalTime.of(15, 30)),
                new OneDayPrayerTime.PrayerTime(OneDayPrayerTime.Time.MAGHRIB, maghribTime),
                new OneDayPrayerTime.PrayerTime(OneDayPrayerTime.Time.ISHA, LocalTime.of(19, 30))));

        when(repository.getOneDayPrayerTime(any(Coordinate.class), any(LocalDate.class))).thenReturn(mockPrayerTime);
        when(hijriDateCalculator.calculateHijriDate(eq(date), eq(maghribTime))).thenReturn("25 Rajab 1446 H");

        // Act
        OneDayPrayerTimeDto result = service.execute(-6.1, 106.8, date);

        // Assert
        assertNotNull(result.hijriDate());
        assertEquals("25 Rajab 1446 H", result.hijriDate());
    }
}
