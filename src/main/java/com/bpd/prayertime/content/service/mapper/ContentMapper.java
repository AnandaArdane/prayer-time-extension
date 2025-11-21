package com.bpd.prayertime.content.service.mapper;

import com.bpd.prayertime.content.entity.Content;
import com.bpd.prayertime.content.service.ContentRequestDto;
import com.bpd.prayertime.content.service.ContentResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    Content toEntity(ContentRequestDto contentRequestDto);
    ContentResponseDto toDto(Content content);
}
