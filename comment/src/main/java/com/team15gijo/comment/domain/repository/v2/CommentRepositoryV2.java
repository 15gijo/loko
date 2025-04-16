package com.team15gijo.comment.domain.repository.v2;

import com.team15gijo.comment.domain.model.v2.CommentV2;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepositoryV2 extends JpaRepository<CommentV2, UUID> {
    // 특정 게시글에 속한 댓글 목록 조회 (페이징 지원)
    Page<CommentV2> findByPostId(UUID postId, Pageable pageable);
}
