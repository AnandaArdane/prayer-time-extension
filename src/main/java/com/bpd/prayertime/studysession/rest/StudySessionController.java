package com.bpd.prayertime.studysession.rest;

import com.bpd.prayertime.studysession.service.StudySessionDto;
import com.bpd.prayertime.studysession.service.StudySessionRequestDto;
import com.bpd.prayertime.studysession.service.StudySessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping("/study-sessions")
    public ResponseEntity<StudySessionDto> create(@Valid @RequestBody StudySessionRequestDto studySessionRequestDto) {
        StudySessionDto studySessionDto = studySessionService.save(studySessionRequestDto);
        return ResponseEntity.ok(studySessionDto);
    }

    @PutMapping("/study-sessions/{id}")
    public ResponseEntity<StudySessionDto> update(@PathVariable Long id, @Valid @RequestBody StudySessionRequestDto studySessionRequestDto) {
        StudySessionDto studySessionDto = studySessionService.update(id, studySessionRequestDto);
        return ResponseEntity.ok(studySessionDto);
    }

    @DeleteMapping("/study-sessions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studySessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
