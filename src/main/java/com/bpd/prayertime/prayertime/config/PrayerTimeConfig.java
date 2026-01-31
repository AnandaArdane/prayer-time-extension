package com.bpd.prayertime.prayertime.config;

import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCity;
import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCoordinate;
import com.bpd.prayertime.prayertime.application.HijriDateCalculator;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
public class PrayerTimeConfig {

    @Bean
    public GetOneDayPrayerTimeByCity getOneDayPrayerTimeByCity(OneDayPrayerTimeRepository oneDayPrayerTimeRepository,
            HijriDateCalculator hijriDateCalculator) {
        return new GetOneDayPrayerTimeByCity(oneDayPrayerTimeRepository, hijriDateCalculator);
    }

    @Bean
    public GetOneDayPrayerTimeByCoordinate getOneDayPrayerTimeByCoordinate(
            OneDayPrayerTimeRepository oneDayPrayerTimeRepository, HijriDateCalculator hijriDateCalculator) {
        return new GetOneDayPrayerTimeByCoordinate(oneDayPrayerTimeRepository, hijriDateCalculator);
    }

    @Bean
    public HijriDateCalculator hijriDateCalculator(Clock clock) {
        return new HijriDateCalculator(clock);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
