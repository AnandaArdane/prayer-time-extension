package com.bpd.prayertime.prayertime.config;

import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCity;
import com.bpd.prayertime.prayertime.application.GetOneDayPrayerTimeByCoordinate;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PrayerTimeConfig {

    @Bean
    public GetOneDayPrayerTimeByCity getOneDayPrayerTimeByCity(OneDayPrayerTimeRepository oneDayPrayerTimeRepository) {
        return new GetOneDayPrayerTimeByCity(oneDayPrayerTimeRepository);
    }

    @Bean
    public GetOneDayPrayerTimeByCoordinate getOneDayPrayerTimeByCoordinate(OneDayPrayerTimeRepository oneDayPrayerTimeRepository) {
        return new GetOneDayPrayerTimeByCoordinate(oneDayPrayerTimeRepository);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
