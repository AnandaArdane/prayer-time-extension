package com.bpd.prayertime.prayertime.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
public class AladhanService {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final int KEMENAG_METHOD = 20;

    private final RestTemplate restTemplate;

    public AladhanService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> getTimingsByCoordinate(Double latitude, Double longitude, LocalDate date) {
        String aladhanDate = DATE_FORMATTER.format(date);
        String url = String.format("%s/timings/%s?latitude=%s&longitude=%s&method=%s", "https://api.aladhan.com/v1", aladhanDate, latitude, longitude, KEMENAG_METHOD);
        PrayerTimeResponse response = restTemplate.getForObject(url, PrayerTimeResponse.class);
        return Optional.ofNullable(response).map(PrayerTimeResponse::data).map(PrayerTimeResponse.Data::timings).orElseThrow();
    }

    public Map<String, String> getTimingsByCity(String city, LocalDate date) {
        String aladhanDate = DATE_FORMATTER.format(date);
        String url = String.format("%s/timingsByCity/%s?country=ID&city=%s&method=%s", "https://api.aladhan.com/v1", aladhanDate, city, KEMENAG_METHOD);
        PrayerTimeResponse response = restTemplate.getForObject(url, PrayerTimeResponse.class);
        return Optional.ofNullable(response).map(PrayerTimeResponse::data).map(PrayerTimeResponse.Data::timings).orElseThrow();
    }

    public record PrayerTimeResponse(Data data) {
        public record Data(@JsonProperty("timings") Map<String, String> timings) {}
    }
}
