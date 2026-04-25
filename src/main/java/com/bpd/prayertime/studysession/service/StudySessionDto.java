package com.bpd.prayertime.studysession.service;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record StudySessionDto(Long id, String place, @NotNull LocalDate date, @NotNull LocalTime time, @NotNull String title, String url) {}
