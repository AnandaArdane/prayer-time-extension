package com.bpd.prayertime.studysession.service.mapper;

import com.bpd.prayertime.studysession.entity.StudySession;
import com.bpd.prayertime.studysession.service.StudySessionDto;
import com.bpd.prayertime.studysession.service.StudySessionRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudySessionMapper {
    StudySession toEntity(StudySessionRequestDto studySessionRequestDto);
    StudySessionDto toDto(StudySession studySession);
}
