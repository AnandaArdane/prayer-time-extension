package com.bpd.prayertime.content.service;

import com.bpd.prayertime.content.entity.Content;
import com.bpd.prayertime.content.repository.ContentRepository;
import com.bpd.prayertime.content.service.mapper.ContentMapper;
import com.bpd.prayertime.content.service.util.ContentFileValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final ContentStorage contentStorage;

    public ContentService(ContentRepository contentRepository, ContentMapper contentMapper,
            ContentStorage contentStorage) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
        this.contentStorage = contentStorage;
    }

    public ContentResponseDto create(ContentRequestDto contentRequestDto, MultipartFile file) {
        ContentFileValidation.validateFile(file);
        Content content = contentMapper.toEntity(contentRequestDto);
        contentRepository.save(content);
        Long contentId = content.getId();

        storeFile(file, contentId);
        ContentResponseDto response = contentMapper.toDto(content);
        response.setUrl(getContentUrl(contentId));
        return response;
    }

    private static String getContentUrl(Long contentId) {
        return "/api/contents/" + contentId + "/file";
    }

    private void storeFile(MultipartFile file, Long contentId) {
        try {
            InputStream inputStream = file.getInputStream();
            contentStorage.store(contentId, inputStream, file.getOriginalFilename(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ContentResponseDto update(Long id, ContentRequestDto contentRequestDto, MultipartFile file) {
        boolean exists = contentRepository.existsById(id);
        if (!exists) {
            throw new IllegalArgumentException("Content not found");
        }

        Content content = contentMapper.toEntity(contentRequestDto);
        content.setId(id);
        contentRepository.save(content);
        Long contentId = content.getId();

        if (file != null) {
            ContentFileValidation.validateFile(file);
            storeFile(file, id);
        }
        ContentResponseDto response = contentMapper.toDto(content);
        response.setUrl(getContentUrl(contentId));
        return response;
    }

    public void delete(Long id) {
        contentRepository.deleteById(id);
        contentStorage.deleteById(id);
    }

    public InputStream retrieveContent(Long id) {
        return contentStorage.retrieve(id);
    }

    public List<ContentResponseDto> findAllActive() {
        return contentRepository.findAllByActiveTrue().stream()
                .map(content -> {
                    ContentResponseDto response = contentMapper.toDto(content);
                    response.setUrl(getContentUrl(content.getId()));
                    return response;
                })
                .toList();
    }
}
