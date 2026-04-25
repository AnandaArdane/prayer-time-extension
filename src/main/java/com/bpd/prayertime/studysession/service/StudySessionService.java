package com.bpd.prayertime.studysession.service;

import com.bpd.prayertime.content.service.util.ContentFileValidation;
import com.bpd.prayertime.studysession.entity.StudySession;
import com.bpd.prayertime.studysession.repository.StudySessionRepository;
import com.bpd.prayertime.studysession.service.mapper.StudySessionMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class StudySessionService {

    private final StudySessionMapper studySessionMapper;
    private final StudySessionRepository studySessionRepository;
    private final StudySessionStorage studySessionStorage;

    public StudySessionService(StudySessionMapper studySessionMapper, StudySessionRepository studySessionRepository, StudySessionStorage studySessionStorage) {
        this.studySessionMapper = studySessionMapper;
        this.studySessionRepository = studySessionRepository;
        this.studySessionStorage = studySessionStorage;
    }

    public List<StudySessionDto> findAll() {
        return studySessionRepository.findAllByOrderByDateDescTimeDesc().stream()
                .map(this::toDtoWithUrl)
                .collect(Collectors.toList());
    }

    public List<StudySessionDto> findAllActive() {
        return studySessionRepository.findByDateAfterOrderByDateAscTimeAsc(LocalDate.now().minusDays(1)).stream()
                .map(this::toDtoWithUrl)
                .collect(Collectors.toList());
    }

    public StudySessionDto save(StudySessionRequestDto studySessionRequestDto, MultipartFile file) {
        StudySession newStudySession = studySessionMapper.toEntity(studySessionRequestDto);
        
        if (file != null && !file.isEmpty()) {
            ContentFileValidation.validateFile(file);
            newStudySession.setHasImage(true);
        } else {
            newStudySession.setHasImage(false);
        }

        StudySession savedStudySession = studySessionRepository.save(newStudySession);
        
        if (savedStudySession.getHasImage()) {
            storeFile(file, savedStudySession.getId());
        }

        return toDtoWithUrl(savedStudySession);
    }

    public StudySessionDto update(Long id, StudySessionRequestDto studySessionRequestDto, MultipartFile file) {
        StudySession existing = studySessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study session not found"));

        StudySession updatedStudySession = studySessionMapper.toEntity(studySessionRequestDto);
        updatedStudySession.setId(id);
        
        // Preserve hasImage if no new file is uploaded
        if (file != null && !file.isEmpty()) {
            ContentFileValidation.validateFile(file);
            updatedStudySession.setHasImage(true);
        } else {
            updatedStudySession.setHasImage(existing.getHasImage());
        }

        StudySession savedStudySession = studySessionRepository.save(updatedStudySession);

        if (file != null && !file.isEmpty()) {
            storeFile(file, savedStudySession.getId());
        }

        return toDtoWithUrl(savedStudySession);
    }

    public void delete(Long id) {
        StudySession existing = studySessionRepository.findById(id).orElse(null);
        if (existing != null) {
            studySessionRepository.deleteById(id);
            if (existing.getHasImage()) {
                studySessionStorage.deleteById(id);
            }
        }
    }

    public InputStream retrieveFile(Long id) {
        return studySessionStorage.retrieve(id);
    }

    private void storeFile(MultipartFile file, Long id) {
        try {
            InputStream inputStream = file.getInputStream();
            studySessionStorage.store(id, inputStream, file.getOriginalFilename(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStudySessionUrl(Long id) {
        return "/api/study-sessions/" + id + "/file";
    }

    private StudySessionDto toDtoWithUrl(StudySession studySession) {
        StudySessionDto dto = studySessionMapper.toDto(studySession);
        if (studySession.getHasImage() != null && studySession.getHasImage()) {
            return new StudySessionDto(dto.id(), dto.place(), dto.date(), dto.time(), dto.title(), getStudySessionUrl(studySession.getId()));
        }
        return dto;
    }
}
