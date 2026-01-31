package com.bpd.prayertime.prayertime.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HijriDateCalculatorTest {

    @Mock
    private Clock clock;

    @InjectMocks
    private HijriDateCalculator calculator;

    @Test
    void calculateHijriDate_BeforeMaghrib() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 1, 25);
        LocalTime maghribTime = LocalTime.of(18, 15);

        // Mock clock to 17:00 on the same date
        Instant fixedInstant = Instant.parse("2025-01-25T17:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        // Act
        String result = calculator.calculateHijriDate(date, maghribTime);

        // Assert
        // 2025-01-25 is 25 Rajab 1446
        assertTrue(result.contains("25"));
        assertTrue(result.contains("Rajab"));
        assertTrue(result.contains("1446"));
    }

    @Test
    void calculateHijriDate_AfterMaghrib() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 1, 25);
        LocalTime maghribTime = LocalTime.of(18, 15);

        // Mock clock to 19:00 on the same date (after Maghrib)
        Instant fixedInstant = Instant.parse("2025-01-25T19:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        // Act
        String result = calculator.calculateHijriDate(date, maghribTime);

        // Assert
        // 2025-01-25 after Maghrib should be 26 Rajab 1446
        assertTrue(result.contains("26"));
        assertTrue(result.contains("Rajab"));
        assertTrue(result.contains("1446"));
    }
}
