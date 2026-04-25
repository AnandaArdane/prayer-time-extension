package com.bpd.prayertime.studysession.rest;

import com.bpd.prayertime.studysession.service.StudySessionDto;
import com.bpd.prayertime.studysession.service.StudySessionRequestDto;
import com.bpd.prayertime.studysession.service.StudySessionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/api")
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @PostMapping("/study-sessions")
    public ResponseEntity<StudySessionDto> create(@Valid @RequestPart("studySessionRequestDto") StudySessionRequestDto studySessionRequestDto,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        StudySessionDto studySessionDto = studySessionService.save(studySessionRequestDto, file);
        return ResponseEntity.ok(studySessionDto);
    }

    @PutMapping("/study-sessions/{id}")
    public ResponseEntity<StudySessionDto> update(@PathVariable Long id,
                                                  @Valid @RequestPart("studySessionRequestDto") StudySessionRequestDto studySessionRequestDto,
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        StudySessionDto studySessionDto = studySessionService.update(id, studySessionRequestDto, file);
        return ResponseEntity.ok(studySessionDto);
    }

    @DeleteMapping("/study-sessions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studySessionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/study-sessions")
    public ResponseEntity<List<StudySessionDto>> listAll() {
        return ResponseEntity.ok(studySessionService.findAll());
    }

    @GetMapping("/study-sessions/active")
    public ResponseEntity<List<StudySessionDto>> listActive() {
        return ResponseEntity.ok(studySessionService.findAllActive());
    }

    @GetMapping("/study-sessions/{id}/file")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Long id) {
        java.io.InputStream inputStream = studySessionService.retrieveFile(id);
        return ResponseEntity.ok()
                .contentType(IMAGE_JPEG)
                .body(new InputStreamResource(inputStream));
    }
}
