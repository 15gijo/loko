package com.team15gijo.post.domain.repository.v2;

import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepositoryV2 extends JpaRepository<PostV2, UUID>, PostQueryDslRepositoryV2Custom {

    // Fetch Join 적용하여 한 번의 쿼리로 모두 조회
    @Query("SELECT p FROM PostV2 p LEFT JOIN FETCH p.hashtags WHERE p.postId = :postId")
    Optional<PostV2> findByIdWithHashtags(@Param("postId") UUID postId);

    List<PostV2> findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(String region, LocalDateTime cursor, PageRequest of);

    @Modifying
    @Query("UPDATE PostV2 p SET p.commentCount = p.commentCount + :delta WHERE p.postId = :postId")
    int incrementCommentCount(@Param("postId") UUID postId,
            @Param("delta") int delta);
    /**
     *  검색에서 쓰는 쿼리
     */
    @Query("""
        SELECT DISTINCT p FROM PostV2 p
        LEFT JOIN p.hashtags h
        WHERE p.region = :region
          AND (
                LOWER(p.username) LIKE LOWER(:keyword)
             OR LOWER(p.postContent) LIKE LOWER(:keyword)
          )
          AND p.createdAt < :cursor
        ORDER BY p.createdAt DESC
    """)
    List<PostV2> findPostByKeyword(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable
    );



}
