package com.bpd.prayertime.content.service;

import java.io.InputStream;

public interface ContentStorage {
    void store(Long id, InputStream inputStream, String fileName, long fileSize);

    void deleteById(Long id);
}
