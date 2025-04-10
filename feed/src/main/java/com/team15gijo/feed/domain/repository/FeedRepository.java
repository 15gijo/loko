package com.team15gijo.feed.domain.repository;

import com.team15gijo.feed.domain.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {
}
