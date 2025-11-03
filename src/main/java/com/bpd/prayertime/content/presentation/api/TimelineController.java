package com.bpd.prayertime.content.presentation.api;

import com.bpd.prayertime.content.application.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timelines")
public class TimelineController {

    private final GetContentsOfTheDayAt getContentsOfTheDayAt;

    public TimelineController(GetContentsOfTheDayAt getContentsOfTheDayAt) {
        this.getContentsOfTheDayAt = getContentsOfTheDayAt;
    }

    @GetMapping
    public List<TimelineDto> createContent(@RequestParam LocalDate date) {
        return getContentsOfTheDayAt.execute(date);
    }
}
