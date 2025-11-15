package com.bpd.prayertime.studysession.service;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record StudySessionRequestDto(String place, @NotNull LocalDate date, @NotNull LocalTime time, @NotNull String title) {}
