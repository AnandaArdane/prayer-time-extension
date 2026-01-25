package com.bpd.prayertime.content.service;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class ContentResponseDto {
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Boolean active;

    @NotNull
    private String url;
}
