package com.bpd.prayertime.content.service;

import com.bpd.prayertime.content.entity.Content;
import com.bpd.prayertime.content.repository.ContentRepository;
import com.bpd.prayertime.content.service.mapper.ContentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentMapper contentMapper;

    @Mock
    private ContentStorage contentStorage;

    @InjectMocks
    private ContentService contentService;

    @Test
    void findAllActive_shouldReturnOnlyActiveContent() {
        // Arrange
        Content activeContent = new Content();
        activeContent.setId(1L);
        activeContent.setTitle("Active Content");
        activeContent.setActive(true);

        ContentResponseDto responseDto = new ContentResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Active Content");

        when(contentRepository.findAllByActiveTrue()).thenReturn(List.of(activeContent));
        when(contentMapper.toDto(activeContent)).thenReturn(responseDto);

        // Act
        List<ContentResponseDto> result = contentService.findAllActive();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Active Content", result.get(0).getTitle());
        assertEquals("/api/contents/1/file", result.get(0).getUrl());
    }
}
