package com.bpd.prayertime.prayertime.infrastructure.thirdparty;

import com.bpd.prayertime.prayertime.domain.City;
import com.bpd.prayertime.prayertime.domain.Coordinate;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTime;
import com.bpd.prayertime.prayertime.domain.OneDayPrayerTimeRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AladhanService implements OneDayPrayerTimeRepository {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final RestTemplate restTemplate;

    public AladhanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OneDayPrayerTime getOneDayPrayerTime(Coordinate coordinate, LocalDate date) {
        String aladhanDate = DATE_FORMATTER.format(date);

        String url = String.format("%s/timings/%s?latitude=%s&longitude=%s&method=%s", "https://api.aladhan.com/v1", aladhanDate, coordinate.latitude().value(), coordinate.longitude().value(), 20);
        PrayerTimeResponse response = restTemplate.getForObject(url, PrayerTimeResponse.class);

        PrayerTimeResponse.Data data = Optional.ofNullable(response).map(PrayerTimeResponse::data).orElseThrow();
        Map<String, String> timings = data.timings;

        System.out.println(timings);
        List<OneDayPrayerTime.PrayerTime> prayerTimes = timings.entrySet().stream()
                .map(this::getPrayerTime)
                .flatMap(Optional::stream)
                .toList();

        return new OneDayPrayerTime(prayerTimes);
    }

    @Override
    public OneDayPrayerTime getOneDayPrayerTime(City city, LocalDate date) {
        String aladhanDate = DATE_FORMATTER.format(date);

        String url = String.format("%s/timingsByCity/%s?country=ID&city=%s&method=%s", "https://api.aladhan.com/v1", aladhanDate, city.name(), 20);
        PrayerTimeResponse response = restTemplate.getForObject(url, PrayerTimeResponse.class);

        PrayerTimeResponse.Data data = Optional.ofNullable(response).map(PrayerTimeResponse::data).orElseThrow();
        Map<String, String> timings = data.timings;

        System.out.println(timings);
        List<OneDayPrayerTime.PrayerTime> prayerTimes = timings.entrySet().stream()
                .map(this::getPrayerTime)
                .flatMap(Optional::stream)
                .toList();

        return new OneDayPrayerTime(prayerTimes);
    }

    private Optional<OneDayPrayerTime.PrayerTime> getPrayerTime(Map.Entry<String, String> entry) {
        OneDayPrayerTime.Time prayerTime = getPrayerTime(entry.getKey()).orElse(null);
        if (prayerTime == null) {
            return Optional.empty();
        }
        LocalTime time = new Time(entry.getValue()).getLocalTime();
        OneDayPrayerTime.PrayerTime prayerTimeResult = new OneDayPrayerTime.PrayerTime(prayerTime, time);
        return Optional.of(prayerTimeResult);
    }

    private Optional<OneDayPrayerTime.Time> getPrayerTime(String key) {
        if (key.equals("Fajr")) {
            return Optional.of(OneDayPrayerTime.Time.SHUBUH);
        }

        if (key.equals("Dhuhr")) {
            return Optional.of(OneDayPrayerTime.Time.DHUHR);
        }

        if (key.equals("Asr")) {
            return Optional.of(OneDayPrayerTime.Time.ASR);
        }

        if (key.equals("Maghrib")) {
            return Optional.of(OneDayPrayerTime.Time.MAGHRIB);
        }

        if (key.equals("Isha")) {
            return Optional.of(OneDayPrayerTime.Time.ISHA);
        }

        return Optional.empty();
    }

    public record PrayerTimeResponse(Data data) {
        public PrayerTimeResponse {
            if (data == null) {
                throw new IllegalArgumentException("Data is null");
            }
        }

        public record Data(
                @JsonProperty("timings")
                Map<String, String> timings
        ) {}
    }

    public record Time(String value) {
        public Time {
            if (value == null) {
                throw new IllegalArgumentException("Value should not be null");
            }
        }
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        public LocalTime getLocalTime() {
            return LocalTime.parse(value, formatter);
        }
    }
}
