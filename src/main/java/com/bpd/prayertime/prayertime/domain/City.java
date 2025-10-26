package com.bpd.prayertime.prayertime.domain;

public record City(String name) {
    public City {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name should not be null");
        }
    }
}
