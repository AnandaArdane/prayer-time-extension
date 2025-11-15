package com.bpd.prayertime.studysession.service;

import com.bpd.prayertime.studysession.entity.StudySession;
import com.bpd.prayertime.studysession.repository.StudySessionRepository;
import com.bpd.prayertime.studysession.service.mapper.StudySessionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudySessionService {

    private final StudySessionMapper studySessionMapper;
    private final StudySessionRepository studySessionRepository;

    public StudySessionService(StudySessionMapper studySessionMapper, StudySessionRepository studySessionRepository) {
        this.studySessionMapper = studySessionMapper;
        this.studySessionRepository = studySessionRepository;
    }

    public StudySessionDto save(StudySessionRequestDto studySessionRequestDto) {
        StudySession newStudySession = studySessionMapper.toEntity(studySessionRequestDto);
        StudySession savedStudySession = studySessionRepository.save(newStudySession);
        return studySessionMapper.toDto(savedStudySession);
    }

    public StudySessionDto update(Long id, StudySessionRequestDto studySessionRequestDto) {
        boolean exists = studySessionRepository.existsById(id);
        if (!exists) {
            throw new IllegalArgumentException("Study session not found");
        }

        StudySession updatedStudySession = studySessionMapper.toEntity(studySessionRequestDto);
        updatedStudySession.setId(id);

        StudySession savedStudySession = studySessionRepository.save(updatedStudySession);
        return studySessionMapper.toDto(savedStudySession);
    }

    public void delete(Long id) {
        studySessionRepository.deleteById(id);
    }
}
