package com.bpd.prayertime.content.rest;

import com.bpd.prayertime.content.service.ContentRequestDto;
import com.bpd.prayertime.content.service.ContentResponseDto;
import com.bpd.prayertime.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/contents")
    public ResponseEntity<ContentResponseDto> create(@Valid @RequestPart ContentRequestDto contentRequestDto, @RequestPart MultipartFile file) {
        ContentResponseDto contentResponseDto = contentService.create(contentRequestDto, file);
        return ResponseEntity.ok(contentResponseDto);
    }

    @PutMapping("/contents/{id}")
    public ResponseEntity<ContentResponseDto> update(@PathVariable Long id, @Valid @RequestPart ContentRequestDto contentRequestDto, @RequestPart MultipartFile file) {
        ContentResponseDto studySessionDto = contentService.update(id, contentRequestDto, file);
        return ResponseEntity.ok(studySessionDto);
    }

    @DeleteMapping("/contents/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
