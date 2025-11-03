package com.bpd.prayertime.content.domain;

import java.time.LocalDate;
import java.util.List;

public interface ContentRepository {
    List<Content> getContentAt(LocalDate localDate);

    Content save(Content content);
}
