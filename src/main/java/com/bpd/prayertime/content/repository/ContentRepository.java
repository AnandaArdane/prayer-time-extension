package com.bpd.prayertime.content.repository;

import com.bpd.prayertime.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByActiveTrue();
}
