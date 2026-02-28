package com.bpd.prayertime.studysession.service;

import com.bpd.prayertime.studysession.entity.StudySession;
import com.bpd.prayertime.studysession.repository.StudySessionRepository;
import com.bpd.prayertime.studysession.service.mapper.StudySessionMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<StudySessionDto> findAll() {
        return studySessionRepository.findAllByOrderByDateDescTimeDesc().stream()
                .map(studySessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<StudySessionDto> findAllActive() {
        return studySessionRepository.findByDateAfterOrderByDateAscTimeAsc(LocalDate.now().minusDays(1)).stream()
                .map(studySessionMapper::toDto)
                .collect(Collectors.toList());
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
