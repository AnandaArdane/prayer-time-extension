package com.bpd.prayertime.content.service;

import jakarta.validation.constraints.NotNull;

public record ContentRequestDto(
        @NotNull
        String title,

        @NotNull
        Boolean active
) {
}
