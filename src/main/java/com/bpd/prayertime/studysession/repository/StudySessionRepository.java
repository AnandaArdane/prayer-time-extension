package com.bpd.prayertime.studysession.repository;

import com.bpd.prayertime.studysession.entity.StudySession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByDateAfterOrderByDateAscTimeAsc(LocalDate date);

    List<StudySession> findAllByOrderByDateDescTimeDesc();
}
