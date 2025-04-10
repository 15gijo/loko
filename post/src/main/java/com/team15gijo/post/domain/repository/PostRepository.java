package com.team15gijo.post.domain.repository;

import com.team15gijo.post.domain.model.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(String region, LocalDateTime cursor, PageRequest of);

    /**
     *  검색에서 쓰는 쿼리
     */
    @Query("""
        SELECT p FROM Post p
        LEFT JOIN FETCH p.hashtags
        WHERE p.region = :region
          AND (p.username LIKE %:keyword% OR p.postContent LIKE %:keyword%)
          AND p.createdAt < :cursor
        ORDER BY p.createdAt DESC
    """)
    List<Post> findPostByKeywordAfter(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable
    );
}
