package com.bpd.prayertime.studysession.service;

import java.io.InputStream;

public interface StudySessionStorage {
    void store(Long id, InputStream inputStream, String fileName, long fileSize);
    void deleteById(Long id);
    InputStream retrieve(Long id);
}
