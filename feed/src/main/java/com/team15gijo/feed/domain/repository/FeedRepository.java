package com.team15gijo.feed.domain.repository;

import com.team15gijo.feed.domain.model.Feed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {
    List<Feed> findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(String region, LocalDateTime cursor, PageRequest of);
}
